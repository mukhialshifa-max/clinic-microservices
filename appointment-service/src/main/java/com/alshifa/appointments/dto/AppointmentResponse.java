package com.alshifa.appointments.dto;

import com.alshifa.appointments.Appointment;
import com.alshifa.appointments.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(Long id, Long patientId, String providerName,
                                  LocalDateTime startTime, AppointmentStatus status) {

    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(a.getId(), a.getPatientId(), a.getProviderName(), a.getStartTime(), a.getStatus());
    }
}
