package com.alshifa.appointments;

import com.alshifa.appointments.client.PatientClient;
import com.alshifa.appointments.dto.AppointmentDetails;
import com.alshifa.appointments.dto.AppointmentRequest;
import com.alshifa.appointments.dto.AppointmentResponse;
import com.alshifa.appointments.dto.PatientDto;
import com.alshifa.appointments.error.AppointmentException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository repository;
    private final PatientClient patientClient;

    public AppointmentService(AppointmentRepository repository, PatientClient patientClient) {
        this.repository = repository;
        this.patientClient = patientClient;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll(Long patientId) {
        List<Appointment> list = (patientId == null)
                ? repository.findAllByOrderByStartTimeAsc()
                : repository.findByPatientIdOrderByStartTimeAsc(patientId);
        return list.stream().map(AppointmentResponse::from).toList();
    }

    /** Appointment plus live patient details from patient-service. */
    @Transactional(readOnly = true)
    public AppointmentDetails getDetails(Long id) {
        Appointment appointment = getOrThrow(id);
        PatientDto patient = patientClient.getPatient(appointment.getPatientId());
        return new AppointmentDetails(AppointmentResponse.from(appointment), patient);
    }

    public AppointmentResponse book(AppointmentRequest request) {
        // 1. Ask patient-service whether this patient exists (throws if not).
        patientClient.getPatient(request.patientId());

        // 2. Stop double-booking the provider.
        String provider = request.providerName().trim();
        if (repository.existsByProviderNameIgnoreCaseAndStartTimeAndStatus(
                provider, request.startTime(), AppointmentStatus.BOOKED)) {
            throw new AppointmentException(HttpStatus.CONFLICT,
                    provider + " is already booked at " + request.startTime());
        }

        // 3. Save.
        Appointment saved = repository.save(new Appointment(request.patientId(), provider, request.startTime()));
        return AppointmentResponse.from(saved);
    }

    public AppointmentResponse cancel(Long id) {
        Appointment appointment = getOrThrow(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppointmentException(HttpStatus.CONFLICT, "Appointment " + id + " is already cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointment);
    }

    private Appointment getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new AppointmentException(HttpStatus.NOT_FOUND, "Appointment " + id + " not found"));
    }
}
