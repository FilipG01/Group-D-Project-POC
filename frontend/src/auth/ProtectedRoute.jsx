import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../auth/useAuth.js";

function ProtectedRoute({ children, allowedRoles }) {
    const { user, loading } = useAuth();
    const location = useLocation();

    if (loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return (
            <Navigate
                to="/login"
                replace
                state={{ from: location }}
            />
        );
    }

    if (
        allowedRoles?.length > 0 &&
        !allowedRoles.includes(user.role)
    ) {
        if (user.role === "ADMIN") {
            return <Navigate to="/admin" replace />;
        }

        if (user.role === "THERAPIST") {
            return <Navigate to="/therapist" replace />;
        }

        return <Navigate to="/dashboard" replace />;
    }

    return children;
}

export default ProtectedRoute;