package com.alshifa.appointments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByOrderByStartTimeAsc();

    List<Appointment> findByPatientIdOrderByStartTimeAsc(Long patientId);

    boolean existsByProviderNameIgnoreCaseAndStartTimeAndStatus(
            String providerName, LocalDateTime startTime, AppointmentStatus status);
}
