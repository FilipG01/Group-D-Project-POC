import { apiRequest } from "./apiClient";

export function reqisterClient(data){
    return apiRequest("/api/auth/register/client",{
        method: "POST",
        body: JSON.stringify(data),
    });
}

export function login(data){
    return apiRequest("/api/auth/login",{
        method: "POST",
        body: JSON.stringify(data),
    });
}

export function logout(){
    return apiRequest("/api/auth/logout", {
        method: "POST",
    });
}

export function getCurrentUser(){
    return apiRequest("/api/auth/me");
}