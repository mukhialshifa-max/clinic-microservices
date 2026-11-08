package com.alshifa.appointments;

import com.alshifa.appointments.client.PatientClient;
import com.alshifa.appointments.dto.AppointmentDetails;
import com.alshifa.appointments.dto.AppointmentRequest;
import com.alshifa.appointments.dto.AppointmentResponse;
import com.alshifa.appointments.dto.PatientDto;
import com.alshifa.appointments.error.AppointmentException;
import com.alshifa.appointments.error.PatientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Both the database and the other microservice are mocked. */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private PatientClient patientClient;

    @InjectMocks
    private AppointmentService service;

    private final LocalDateTime nineAm = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    private final PatientDto sara = new PatientDto(1L, "Sara", "Ahmed", LocalDate.of(1990, 5, 1), null);

    @Test
    void bookChecksPatientThenSaves() {
        when(patientClient.getPatient(1L)).thenReturn(sara);
        when(repository.existsByProviderNameIgnoreCaseAndStartTimeAndStatus("Dr. Patel", nineAm, AppointmentStatus.BOOKED))
                .thenReturn(false);
        when(repository.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });

        AppointmentResponse response = service.book(new AppointmentRequest(1L, "Dr. Patel", nineAm));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void bookFailsWhenPatientDoesNotExist() {
        when(patientClient.getPatient(99L)).thenThrow(new PatientNotFoundException(99L));

        assertThatThrownBy(() -> service.book(new AppointmentRequest(99L, "Dr. Patel", nineAm)))
                .isInstanceOf(PatientNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void bookFailsWhenProviderAlreadyBooked() {
        when(patientClient.getPatient(1L)).thenReturn(sara);
        when(repository.existsByProviderNameIgnoreCaseAndStartTimeAndStatus("Dr. Patel", nineAm, AppointmentStatus.BOOKED))
                .thenReturn(true);

        assertThatThrownBy(() -> service.book(new AppointmentRequest(1L, "Dr. Patel", nineAm)))
                .isInstanceOf(AppointmentException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void detailsCombineAppointmentAndPatient() {
        Appointment appointment = new Appointment(1L, "Dr. Patel", nineAm);
        appointment.setId(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(appointment));
        when(patientClient.getPatient(1L)).thenReturn(sara);

        AppointmentDetails details = service.getDetails(10L);

        assertThat(details.patient().lastName()).isEqualTo("Ahmed");
        assertThat(details.appointment().providerName()).isEqualTo("Dr. Patel");
    }

    @Test
    void cannotCancelTwice() {
        Appointment appointment = new Appointment(1L, "Dr. Patel", nineAm);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(repository.findById(5L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.cancel(5L)).isInstanceOf(AppointmentException.class);
    }
}
