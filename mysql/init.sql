-- Database-per-service: each microservice gets its own database and user,
-- so neither service can read the other's tables directly.

CREATE DATABASE IF NOT EXISTS patients_db;
CREATE USER IF NOT EXISTS 'patients_user'@'%' IDENTIFIED BY 'patients_pass';
GRANT ALL PRIVILEGES ON patients_db.* TO 'patients_user'@'%';

CREATE DATABASE IF NOT EXISTS appointments_db;
CREATE USER IF NOT EXISTS 'appointments_user'@'%' IDENTIFIED BY 'appointments_pass';
GRANT ALL PRIVILEGES ON appointments_db.* TO 'appointments_user'@'%';

FLUSH PRIVILEGES;
