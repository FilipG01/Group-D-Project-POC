package com.roottherapy.backend.appointments;


import com.roottherapy.backend.appointments.dto.AppointmentResponse;
import com.roottherapy.backend.appointments.dto.CreateAppointmentRequest;
import com.roottherapy.backend.messaging.Conversation;
import com.roottherapy.backend.messaging.ConversationRepository;
import com.roottherapy.backend.messaging.ConversationStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ConversationRepository conversationRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ConversationRepository conversationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.conversationRepository = conversationRepository;
    }

    public AppointmentResponse createClientAppointmentRequest(
            User currentUser,
            CreateAppointmentRequest req
    ){
        if (currentUser.getRole() != UserRole.CLIENT) {
            throw new AccessDeniedException("only clients can request appointments");
        }

        if (!req.scheduledEnd().isAfter(req.scheduledStart())) {
            throw new IllegalArgumentException("appointment end time must be after start time");
        }

        if (req.scheduledStart().isBefore(Instant.now())) {
            throw new IllegalArgumentException("appointment start time must be in the future");
        }

        Conversation conversation = conversationRepository
                .findFirstByClientIdAndStatus(currentUser.getId(), ConversationStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "you need to choose a therapist before booking an appointment"
                ));

        Appointment appointment = appointmentRepository.save(
                new Appointment(
                        currentUser,
                        conversation.getTherapist(),
                        currentUser,
                        req.scheduledStart(),
                        req.scheduledEnd(),
                        req.locationType(),
                        req.clientNotes()
                )
        );

        return AppointmentResponse.from(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listMyAppointments(User currentUser){
        if(currentUser.getRole() == UserRole.CLIENT) {
            return appointmentRepository
                    .findByClientIdOrderByScheduledStartAsc(currentUser.getId())
                    .stream()
                    .map(AppointmentResponse::from)
                    .toList();
        }

        if (currentUser.getRole() == UserRole.THERAPIST) {
            return appointmentRepository
                    .findByTherapistIdOrderByScheduledStartAsc(currentUser.getId())
                    .stream()
                    .map(AppointmentResponse::from)
                    .toList();
        }

        if (currentUser.getRole() == UserRole.ADMIN) {
            return appointmentRepository
                    .findAllByOrderByScheduledStartAsc()
                    .stream()
                    .map(AppointmentResponse::from)
                    .toList();
        }

        throw new AccessDeniedException("cannot view appointments");
    }
}
