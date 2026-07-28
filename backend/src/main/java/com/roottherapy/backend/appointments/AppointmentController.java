package com.roottherapy.backend.appointments;

import com.roottherapy.backend.appointments.dto.AppointmentResponse;
import com.roottherapy.backend.appointments.dto.CancelAppointmentRequest;
import com.roottherapy.backend.appointments.dto.CreateAppointmentRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentResponse> listMyAppointments(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return appointmentService.listMyAppointments(userDetails.getUser());
    }

    @PostMapping
    public AppointmentResponse createAppointment(Authentication auth,
                                                 @Valid @RequestBody CreateAppointmentRequest req){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return appointmentService.createClientAppointmentRequest(userDetails.getUser(), req);
    }

    @PatchMapping("/{appointmentId}/confirm")
    public AppointmentResponse confirmAppointment(
            Authentication auth, @PathVariable UUID appointmentId
    ){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return appointmentService.confirmAppointment(userDetails.getUser(), appointmentId);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(
            Authentication auth, @PathVariable UUID appointmentId,
            @Valid@RequestBody CancelAppointmentRequest req
    ){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return appointmentService.cancelAppointment(
                userDetails.getUser(),
                appointmentId,
                req.cancellationReason()
        );
    }
}
