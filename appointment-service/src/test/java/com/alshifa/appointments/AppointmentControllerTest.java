package com.alshifa.appointments;

import com.alshifa.appointments.error.PatientNotFoundException;
import com.alshifa.appointments.error.PatientServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Checks that errors from the other service become the right HTTP status codes. */
@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AppointmentService service;

    private String body() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        return """
                {"patientId":1,"providerName":"Dr. Patel","startTime":"%s"}
                """.formatted(tomorrow);
    }

    @Test
    void unknownPatientReturns400() throws Exception {
        when(service.book(any())).thenThrow(new PatientNotFoundException(1L));

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Patient 1 does not exist"));
    }

    @Test
    void patientServiceDownReturns503() throws Exception {
        when(service.book(any())).thenThrow(new PatientServiceUnavailableException("patient-service is not reachable"));

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body()))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void missingFieldsReturn400() throws Exception {
        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.patientId").exists());
    }
}
