import { apiRequest } from "./apiClient";

export function listTherapists(){
    return apiRequest("/api/therapists")
}