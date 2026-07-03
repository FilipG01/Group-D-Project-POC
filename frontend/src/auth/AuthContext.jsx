import {createContext, userContext, useEffect, useState } from "react";
import * as authApi from "../api/authApi";

const AuthContext = createContext(null);
export function AuthProvider({ children }){
    const [user, setUser ] = useState(null);
    const [loading, setLoading] = usestate(true);

    async function refreshUser(){
        try{
            const currentUser = await authApi.Api.getCurrentUser();
            setUser(currentUser);
        }catch{
            setUser(null);
        }finally{
            setLoading(false);
        }
    }

    async function loginUser(credentials){
        const loggedInUser = await authApi.login(credentials);
        setUser(loggedInUser);
        return loggedInUser;
    }
    async function logoutUser(){
        await authApi.logout();
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