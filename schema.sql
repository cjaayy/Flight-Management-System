CREATE DATABASE IF NOT EXISTS flight_management_system;
USE flight_management_system;

CREATE TABLE IF NOT EXISTS flights (
  id INT AUTO_INCREMENT PRIMARY KEY,
  flight_date DATE NOT NULL,
  flight_time TIME NOT NULL,
  origin VARCHAR(64) NOT NULL,
  destination VARCHAR(64) NOT NULL,
  flight_number VARCHAR(32) NOT NULL UNIQUE,
  status ENUM('Confirmed', 'Unconfirmed') NOT NULL
);
