package com.alshifa.appointments.error;

public class PatientServiceUnavailableException extends RuntimeException {
    public PatientServiceUnavailableException(String message) {
        super(message);
    }
}
