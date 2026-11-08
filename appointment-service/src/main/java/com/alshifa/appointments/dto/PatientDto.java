package com.alshifa.appointments.dto;

import java.time.LocalDate;

/** The shape of a patient as returned by patient-service's REST API. */
public record PatientDto(Long id, String firstName, String lastName, LocalDate dateOfBirth, String phone) {
}
