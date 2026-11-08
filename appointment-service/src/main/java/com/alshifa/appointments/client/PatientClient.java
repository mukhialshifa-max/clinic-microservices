package com.alshifa.appointments.client;

import com.alshifa.appointments.dto.PatientDto;
import com.alshifa.appointments.error.PatientNotFoundException;
import com.alshifa.appointments.error.PatientServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Calls patient-service over HTTP (service-to-service REST call).
 * The base URL comes from configuration, so it can be "localhost:8081"
 * locally and "patient-service:8081" inside Docker.
 */
@Component
public class PatientClient {

    private final RestClient restClient;

    public PatientClient(@Value("${patient-service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public PatientDto getPatient(Long patientId) {
        try {
            return restClient.get()
                    .uri("/api/patients/{id}", patientId)
                    .retrieve()
                    .body(PatientDto.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new PatientNotFoundException(patientId);
            }
            throw new PatientServiceUnavailableException("patient-service returned " + ex.getStatusCode());
        } catch (ResourceAccessException ex) {
            // connection refused or timed out
            throw new PatientServiceUnavailableException("patient-service is not reachable");
        }
    }
}
