package com.alshifa.appointments;

import com.alshifa.appointments.dto.AppointmentDetails;
import com.alshifa.appointments.dto.AppointmentRequest;
import com.alshifa.appointments.dto.AppointmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    /** GET /api/appointments or /api/appointments?patientId=1 */
    @GetMapping
    public List<AppointmentResponse> list(@RequestParam(required = false) Long patientId) {
        return service.findAll(patientId);
    }

    /** GET /api/appointments/1 — includes the patient's details from patient-service */
    @GetMapping("/{id}")
    public AppointmentDetails get(@PathVariable Long id) {
        return service.getDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@Valid @RequestBody AppointmentRequest request) {
        return service.book(request);
    }

    @PostMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
