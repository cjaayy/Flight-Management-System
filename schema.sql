CREATE DATABASE IF NOT EXISTS flight_management_system;
USE flight_management_system;

CREATE TABLE IF NOT EXISTS flights (
  id INT AUTO_INCREMENT PRIMARY KEY,
  flight_date DATE NOT NULL,
  flight_time TIME NOT NULL,
  origin VARCHAR(64) NOT NULL,
  destination VARCHAR(64) NOT NULL,
  flight_number VARCHAR(32) NOT NULL UNIQUE,
  aircraft VARCHAR(64) NOT NULL,
  status ENUM('Confirmed', 'Unconfirmed') NOT NULL
);

TRUNCATE TABLE flights;

INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (1, '2026-06-01', '08:30:00', 'Manila', 'Cebu', 'PR101', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (2, '2026-06-01', '10:15:00', 'Clark', 'Davao', '5J202', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (3, '2026-06-02', '13:45:00', 'Cebu', 'Iloilo', 'Z2303', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (4, '2026-06-02', '16:20:00', 'Davao', 'Manila', 'PR404', 'Airbus A321', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (5, '2026-06-03', '06:50:00', 'Clark', 'Baguio', 'DG505', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (6, '2026-06-03', '11:10:00', 'Iloilo', 'Cebu', '5J606', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (7, '2026-06-04', '14:30:00', 'Manila', 'Singapore', 'SQ707', 'Airbus A350-900', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (8, '2026-06-04', '18:45:00', 'Singapore', 'Manila', 'SQ708', 'Airbus A350-900', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (9, '2026-06-05', '09:25:00', 'Cebu', 'Hong Kong', 'CX809', 'Airbus A321neo', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (10, '2026-06-05', '21:00:00', 'Hong Kong', 'Cebu', 'CX810', 'Airbus A321neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (11, '2026-06-06', '07:15:00', 'Manila', 'Bacolod', 'PR811', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (12, '2026-06-06', '09:40:00', 'Cebu', 'Tacloban', '5J812', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (13, '2026-06-06', '12:20:00', 'Clark', 'Puerto Princesa', 'DG813', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (14, '2026-06-06', '15:10:00', 'Davao', 'Cagayan de Oro', 'Z2814', 'Airbus A321', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (15, '2026-06-07', '06:45:00', 'Manila', 'General Santos', 'PR815', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (16, '2026-06-07', '08:55:00', 'Iloilo', 'Manila', '5J816', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (17, '2026-06-07', '11:30:00', 'Clark', 'Cebu', 'DG817', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (18, '2026-06-07', '14:25:00', 'Cebu', 'Davao', 'PR818', 'Airbus A321', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (19, '2026-06-08', '05:50:00', 'Manila', 'Kalibo', 'Z2819', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (20, '2026-06-08', '09:15:00', 'Baguio', 'Clark', 'DG820', 'Airbus A320neo', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (21, '2026-06-08', '13:40:00', 'Tacloban', 'Cebu', '5J821', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (22, '2026-06-08', '17:20:00', 'Singapore', 'Clark', 'SQ822', 'Airbus A350-900', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (23, '2026-06-09', '07:05:00', 'Manila', 'Tokyo', 'NH823', 'Boeing 787-9', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (24, '2026-06-09', '10:50:00', 'Tokyo', 'Manila', 'NH824', 'Boeing 787-9', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (25, '2026-06-09', '14:35:00', 'Cebu', 'Seoul', 'KE825', 'Boeing 737-800', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (26, '2026-06-09', '18:10:00', 'Seoul', 'Cebu', 'KE826', 'Boeing 737-800', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (27, '2026-06-10', '06:30:00', 'Davao', 'Zamboanga', 'PR827', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (28, '2026-06-10', '09:45:00', 'Puerto Princesa', 'Manila', '5J828', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (29, '2026-06-10', '13:15:00', 'Clark', 'Boracay', 'DG829', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (30, '2026-06-10', '16:55:00', 'Boracay', 'Clark', 'DG830', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (31, '2026-06-11', '08:20:00', 'Manila', 'Bangkok', 'TG831', 'Boeing 787-8', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (32, '2026-06-11', '12:10:00', 'Bangkok', 'Manila', 'TG832', 'Boeing 787-8', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (33, '2026-06-11', '15:45:00', 'Cebu', 'Siargao', '5J833', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (34, '2026-06-11', '19:30:00', 'Siargao', 'Cebu', '5J834', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (35, '2026-06-12', '06:15:00', 'Manila', 'Dubai', 'EK835', 'Boeing 777-300ER', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (36, '2026-06-12', '11:50:00', 'Dubai', 'Manila', 'EK836', 'Boeing 777-300ER', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (37, '2026-06-12', '14:25:00', 'Clark', 'Bohol', 'PR837', 'Airbus A321', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (38, '2026-06-12', '18:40:00', 'Bohol', 'Clark', 'PR838', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (39, '2026-06-13', '07:35:00', 'Davao', 'Cebu', 'Z2839', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (40, '2026-06-13', '10:05:00', 'Cebu', 'Manila', 'PR840', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (41, '2026-06-13', '13:20:00', 'Manila', 'Taipei', 'CI841', 'Airbus A321neo', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (42, '2026-06-13', '17:00:00', 'Taipei', 'Manila', 'CI842', 'Airbus A321neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (43, '2026-06-14', '06:40:00', 'Clark', 'Singapore', 'SQ843', 'Airbus A350-900', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (44, '2026-06-14', '10:30:00', 'Singapore', 'Clark', 'SQ844', 'Airbus A350-900', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (45, '2026-06-14', '14:10:00', 'Iloilo', 'Bacolod', 'DG845', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (46, '2026-06-14', '16:45:00', 'Bacolod', 'Iloilo', 'DG846', 'Airbus A320neo', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (47, '2026-06-15', '08:00:00', 'Manila', 'Cagayan de Oro', 'PR847', 'Airbus A320', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (48, '2026-06-15', '11:25:00', 'Cagayan de Oro', 'Manila', 'PR848', 'Airbus A321', 'Confirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (49, '2026-06-15', '15:15:00', 'Cebu', 'Palawan', '5J849', 'Airbus A320', 'Unconfirmed');
INSERT INTO flights (`id`, `flight_date`, `flight_time`, `origin`, `destination`, `flight_number`, `aircraft`, `status`) VALUES (50, '2026-06-15', '19:05:00', 'Palawan', 'Cebu', '5J850', 'Airbus A321', 'Confirmed');
