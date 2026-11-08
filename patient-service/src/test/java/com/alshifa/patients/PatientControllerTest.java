package com.alshifa.patients;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PatientService service;

    @Test
    void getReturnsPatient() throws Exception {
        when(service.findById(1L)).thenReturn(
                new PatientResponse(1L, "Sara", "Ahmed", LocalDate.of(1990, 5, 1), null));

        mvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Ahmed"));
    }

    @Test
    void unknownPatientReturns404() throws Exception {
        when(service.findById(99L)).thenThrow(new PatientNotFoundException(99L));

        mvc.perform(get("/api/patients/99")).andExpect(status().isNotFound());
    }

    @Test
    void invalidPatientReturns400() throws Exception {
        mvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"Ahmed\",\"dateOfBirth\":\"2999-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists());
    }
}
