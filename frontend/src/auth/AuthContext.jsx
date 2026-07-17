import { useEffect, useState } from "react";
import * as apiAuth from "../api/apiAuth";
import AuthContext from "./authContext.js";

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    async function refreshUser() {
        setLoading(true);

        try {
            const currentUser = await apiAuth.getCurrentUser();
            setUser(currentUser);

            return currentUser;
        } catch {
            setUser(null);
            return null;
        } finally {
            setLoading(false);
        }
    }

    async function loginUser(credentials) {
        const loggedInUser = await apiAuth.login(credentials);

        setUser(loggedInUser);

        return loggedInUser;
    }

    async function logoutUser() {
        await apiAuth.logout();
        setUser(null);
    }

    useEffect(() => {
        let isCancelled = false;

        apiAuth
            .getCurrentUser()
            .then((currentUser) => {
                if (!isCancelled) {
                    setUser(currentUser);
                }
            })
            .catch(() => {
                if (!isCancelled) {
                    setUser(null);
                }
            })
            .finally(() => {
                if (!isCancelled) {
                    setLoading(false);
                }
            });

        return () => {
            isCancelled = true;
        };
    }, []);

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                loginUser,
                logoutUser,
                refreshUser,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}