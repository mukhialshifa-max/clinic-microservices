package com.alshifa.appointments.error;

import org.springframework.http.HttpStatus;

/** A business error with the HTTP status it should map to (404 not found, 409 conflict). */
public class AppointmentException extends RuntimeException {

    private final HttpStatus status;

    public AppointmentException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
