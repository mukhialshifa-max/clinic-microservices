package com.alshifa.appointments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Stores only the patient's ID, not the patient's details. The patient record
 * belongs to patient-service; this service asks for it over HTTP when needed.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 100)
    private String providerName;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    protected Appointment() {
        // required by JPA
    }

    public Appointment(Long patientId, String providerName, LocalDateTime startTime) {
        this.patientId = patientId;
        this.providerName = providerName;
        this.startTime = startTime;
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public String getProviderName() { return providerName; }
    public LocalDateTime getStartTime() { return startTime; }
    public AppointmentStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
