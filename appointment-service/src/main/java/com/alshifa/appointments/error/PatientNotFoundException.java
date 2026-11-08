package com.alshifa.appointments.error;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long patientId) {
        super("Patient " + patientId + " does not exist");
    }
}
