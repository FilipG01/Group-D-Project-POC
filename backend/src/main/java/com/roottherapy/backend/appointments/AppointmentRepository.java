package com.roottherapy.backend.appointments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByClientIdOrderByScheduledStartAsc(UUID clientId);
    List<Appointment> findByTherapistIdOrderByScheduledStartAsc(UUID therapistId);
    List<Appointment> findAllByOrderByScheduledStartAsc();
}
