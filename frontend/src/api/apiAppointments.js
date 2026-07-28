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