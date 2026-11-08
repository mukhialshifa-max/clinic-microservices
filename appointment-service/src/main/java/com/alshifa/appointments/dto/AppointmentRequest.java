package com.alshifa.appointments.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull(message = "Patient ID is required") @Positive Long patientId,
        @NotBlank(message = "Provider name is required") @Size(max = 100) String providerName,
        @NotNull(message = "Start time is required") @Future(message = "Start time must be in the future") LocalDateTime startTime
) {
}
