package com.alshifa.patients;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    /** Search by first or last name, e.g. ?search=ahm */
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastNameAsc(
            String firstName, String lastName);

    List<Patient> findAllByOrderByLastNameAsc();
}
