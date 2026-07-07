import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function ClientDashboard() {
    const navigate = useNavigate();
    const { user, logoutUser } = useAuth();

    async function handleLogout() {
        await logoutUser();
        navigate("/login");
    }

    return (
        <main>
            <h2>Client Dashboard</h2>
            <ul>
                <li>Email: {user.email}</li>
                <li>Name: {user.firstName} {user.lastName}</li>
                <li>Role: {user.role}</li>
                <li>Status: {user.accountStatus}</li>
            </ul>

            <button type="button" onClick={handleLogout}>
                Logout
            </button>
        </main>
    );
}

export default ClientDashboard;