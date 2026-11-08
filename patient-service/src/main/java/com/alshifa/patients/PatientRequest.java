package com.alshifa.patients;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank(message = "First name is required") @Size(max = 60) String firstName,
        @NotBlank(message = "Last name is required") @Size(max = 60) String lastName,
        @NotNull(message = "Date of birth is required") @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
        @Pattern(regexp = "^$|^[0-9+()\\- ]{7,20}$", message = "Phone number is not valid") String phone
) {
}
