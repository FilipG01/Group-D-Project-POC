package com.roottherapy.backend.appointments;

import com.roottherapy.backend.appointments.dto.AppointmentResponse;
import com.roottherapy.backend.appointments.dto.CreateAppointmentRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
