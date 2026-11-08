package com.alshifa.patients;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    @Test
    void createTrimsNamesAndReturnsSavedPatient() {
        when(repository.save(any(Patient.class))).thenAnswer(inv -> {
            Patient p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PatientResponse response = service.create(
                new PatientRequest("  Sara ", " Ahmed ", LocalDate.of(1990, 5, 1), "902-555-0101"));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("Sara");
        assertThat(response.lastName()).isEqualTo("Ahmed");
    }

    @Test
    void searchUsesNameQuery() {
        when(repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastNameAsc("ahm", "ahm"))
                .thenReturn(List.of(new Patient("Sara", "Ahmed", LocalDate.of(1990, 5, 1), null)));

        assertThat(service.findAll("ahm")).hasSize(1);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L)).isInstanceOf(PatientNotFoundException.class);
    }
}
