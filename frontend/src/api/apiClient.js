const API_URL = "http://localhost:8080";

export async function apiRequest(path, options ={}){
    const res = await fetch(`${API_URL}${path}`,{
        ...options,
        credentials: "include",
        headers:{
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
    });
    if(!res.ok){
        const message = await res.text();
        throw new Error(message||`Request failst with the status code: ${res.status}`);
    }
    if(res.status === 204){
        return null;
    }

    return res.json();
}