package com.alshifa.patients;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> findAll(String search) {
        List<Patient> patients = (search == null || search.isBlank())
                ? repository.findAllByOrderByLastNameAsc()
                : repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastNameAsc(
                        search.trim(), search.trim());
        return patients.stream().map(PatientResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return PatientResponse.from(getOrThrow(id));
    }

    public PatientResponse create(PatientRequest request) {
        Patient saved = repository.save(new Patient(
                request.firstName().trim(), request.lastName().trim(), request.dateOfBirth(), request.phone()));
        return PatientResponse.from(saved);
    }

    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = getOrThrow(id);
        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setPhone(request.phone());
        return PatientResponse.from(patient);
    }

    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Patient getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
    }
}
