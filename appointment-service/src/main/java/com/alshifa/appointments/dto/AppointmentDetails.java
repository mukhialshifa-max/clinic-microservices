package com.alshifa.appointments.dto;

/** An appointment combined with the patient data fetched from patient-service. */
public record AppointmentDetails(AppointmentResponse appointment, PatientDto patient) {
}
