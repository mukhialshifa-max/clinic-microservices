package com.alshifa.patients;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Patient " + id + " not found");
    }
}
