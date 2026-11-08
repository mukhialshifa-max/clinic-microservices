# Clinic Microservices

![CI](https://github.com/mukhialshifa-max/clinic-microservices/actions/workflows/ci.yml/badge.svg)

Two Spring Boot microservices that work together to book clinic appointments,
each with its own MySQL database, started together with Docker Compose.

**Stack:** Java 17 · Spring Boot 3 · Spring Data JPA · REST · RestClient ·
MySQL · H2 · Docker · Docker Compose · Maven · JUnit 5 · Mockito · GitHub Actions

It builds on my [Clinic Appointment Tracker](https://github.com/mukhialshifa-max/clinic-appointment-tracker)
by splitting the system into independent services.

## Architecture

```
                 ┌──────────────────────┐   GET /api/patients/{id}   ┌───────────────────┐
  client ──────▶ │  appointment-service │ ─────────────────────────▶ │  patient-service  │ ◀── client
                 │        :8082         │                            │       :8081       │
                 └──────────┬───────────┘                            └─────────┬─────────┘
                            │ JPA                                              │ JPA
                    ┌───────▼────────┐                                 ┌───────▼──────┐
                    │ appointments_db│          MySQL 8 container      │  patients_db │
                    └────────────────┘                                 └──────────────┘
```

- **patient-service** owns patient records (create, search, update, delete).
- **appointment-service** books and cancels appointments. Before booking, it calls
  patient-service over HTTP to check that the patient exists.
- **Database per service:** each service has its own database and user, so services
  share data only through their APIs.

### Failure handling between services

| Situation | appointment-service returns |
|-----------|------------------------------|
| Patient does not exist (patient-service 404) | 400 Bad Request |
| patient-service is down or times out | 503 Service Unavailable |
| Provider already booked at that time | 409 Conflict |
| Invalid input | 400 with field errors |

Calls use a 2-second connect and 3-second read timeout, so a slow service
doesn't hang the other.

## Run with Docker (recommended)

You need Docker Desktop.

```bash
docker compose up --build
```

This starts MySQL, patient-service on http://localhost:8081 and
appointment-service on http://localhost:8082. Stop with `Ctrl + C`, then
`docker compose down`.

## Try it

```bash
# 1. Create a patient
curl -X POST http://localhost:8081/api/patients -H "Content-Type: application/json" \
  -d '{"firstName":"Sara","lastName":"Ahmed","dateOfBirth":"1990-05-01","phone":"902-555-0101"}'

# 2. Book an appointment for patient 1
curl -X POST http://localhost:8082/api/appointments -H "Content-Type: application/json" \
  -d '{"patientId":1,"providerName":"Dr. Patel","startTime":"2026-12-01T09:00"}'

# 3. Get the appointment with patient details (calls patient-service)
curl http://localhost:8082/api/appointments/1

# 4. Try booking for a patient that doesn't exist -> 400
curl -X POST http://localhost:8082/api/appointments -H "Content-Type: application/json" \
  -d '{"patientId":999,"providerName":"Dr. Patel","startTime":"2026-12-01T10:00"}'
```

Health checks: http://localhost:8081/actuator/health and http://localhost:8082/actuator/health

## Run without Docker

Each service falls back to an in-memory H2 database. In two terminals:

```bash
cd patient-service && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
```

## API

**patient-service (:8081)**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients?search=ahm` | List or search patients |
| GET | `/api/patients/{id}` | Get one patient |
| POST | `/api/patients` | Create a patient |
| PUT | `/api/patients/{id}` | Update a patient |
| DELETE | `/api/patients/{id}` | Delete a patient |

**appointment-service (:8082)**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/appointments?patientId=1` | List appointments (filter optional) |
| GET | `/api/appointments/{id}` | Appointment + patient details |
| POST | `/api/appointments` | Book (checks patient exists, no double-booking) |
| POST | `/api/appointments/{id}/cancel` | Cancel |

## Tests and CI

```bash
cd patient-service && mvn test
cd appointment-service && mvn test
```

Unit tests mock the database and the other service (Mockito); web tests use MockMvc
to check status codes. GitHub Actions runs both test suites in parallel, then builds
both Docker images, on every push.

## How it was built

Designed the service boundaries and API contract first, then implemented them with
an AI code assistant (Claude) using spec-driven development, with tests for each rule.

## Next steps

- API gateway (Spring Cloud Gateway) as a single entry point
- Retry and circuit breaker with Resilience4j
- Deploy to AWS or OpenShift
