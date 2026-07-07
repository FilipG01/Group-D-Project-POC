import { createContext, useContext, useEffect, useState } from "react";
import * as apiAuth from "../api/apiAuth";

const AuthContext = createContext(null);
export function AuthProvider({ children }){
    const [user, setUser ] = useState(null);
    const [loading, setLoading] = useState(true);

    async function refreshUser(){
        try{
            const currentUser = await apiAuth.getCurrentUser();
            setUser(currentUser);
        }catch{
            setUser(null);
        }finally{
            setLoading(false);
        }
    }

    async function loginUser(credentials){
        const loggedInUser = await apiAuth.login(credentials);
        setUser(loggedInUser);
        return loggedInUser;
    }
    async function logoutUser(){
        await apiAuth.logout();
        setUser(null);
    }

    useEffect(() => {
        refreshUser();
    }, []);
    return (
        <AuthContext.Provider value={{user, loading, loginUser, logoutUser, refreshUser}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(){
    return useContext(AuthContext);
}