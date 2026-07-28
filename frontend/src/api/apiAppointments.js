import { apiRequest } from "./apiClient";

  export function listAppointments() {
      return apiRequest("/api/appointments");
  }

  export function createAppointment(appointmentData) {
      return apiRequest("/api/appointments", {
          method: "POST",
          body: JSON.stringify(appointmentData),
      });
  }

  export function confirmAppointment(appointmentId){
    return apiRequest(`/api/appointments/${appointmentId}/confirm`, {
        method: "PATCH",
    });
  }
  export function cancelAppointment(appointmentId, cancellationReason){
    return apiRequest(`/api/appointments/${appointmentId}/cancel`, {
        method: "PATCH",
        body: JSON.stringify({ cancellationReason }),
    });
  }