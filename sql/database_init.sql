CREATE DATABASE IF NOT EXISTS PoliceDB;
USE PoliceDB;

CREATE TABLE IF NOT EXISTS PERSON (
	person_id VARCHAR(50) PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	age INT NOT NULL,
	phone VARCHAR(15) NOT NULL,
	username VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	role ENUM('POLICE', 'VICTIM', 'ACCUSED') NOT NULL
);

CREATE TABLE IF NOT EXISTS POLICE (
	person_id VARCHAR(50) PRIMARY KEY,
	`rank` VARCHAR(50) NOT NULL,
	department VARCHAR(100) NOT NULL,
	badge_number VARCHAR(50) NOT NULL UNIQUE,
	CONSTRAINT fk_police_person FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);

CREATE TABLE IF NOT EXISTS VICTIM (
	person_id VARCHAR(50) PRIMARY KEY,
	crime_details TEXT NOT NULL,
	emergency_contact VARCHAR(15) NOT NULL,
	CONSTRAINT fk_victim_person FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);

CREATE TABLE IF NOT EXISTS ACCUSED (
	person_id VARCHAR(50) PRIMARY KEY,
	charges TEXT NOT NULL,
	bail_status BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT fk_accused_person FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);

CREATE TABLE IF NOT EXISTS FIR (
	fir_id VARCHAR(50) PRIMARY KEY,
	date DATE NOT NULL,
	location VARCHAR(255) NOT NULL,
	description TEXT NOT NULL,
	victim_id VARCHAR(50) NOT NULL,
	registered_by VARCHAR(50) NOT NULL,
	CONSTRAINT fk_fir_victim FOREIGN KEY (victim_id) REFERENCES VICTIM(person_id),
	CONSTRAINT fk_fir_police FOREIGN KEY (registered_by) REFERENCES POLICE(person_id)
);

CREATE TABLE IF NOT EXISTS `CASE` (
	case_id VARCHAR(50) PRIMARY KEY,
	status ENUM('OPEN', 'CLOSED') NOT NULL DEFAULT 'OPEN',
	description TEXT NOT NULL,
	officer_username VARCHAR(50) NOT NULL,
	victim_username VARCHAR(50) NOT NULL,
	accused_username VARCHAR(50) NOT NULL,
	CONSTRAINT fk_case_officer_username FOREIGN KEY (officer_username) REFERENCES PERSON(username),
	CONSTRAINT fk_case_victim_username FOREIGN KEY (victim_username) REFERENCES PERSON(username),
	CONSTRAINT fk_case_accused_username FOREIGN KEY (accused_username) REFERENCES PERSON(username)
);

CREATE TABLE IF NOT EXISTS CASE_ACCUSED (
	case_id VARCHAR(50) NOT NULL,
	accused_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (case_id, accused_id),
	CONSTRAINT fk_case_accused_case FOREIGN KEY (case_id) REFERENCES `CASE`(case_id),
	CONSTRAINT fk_case_accused_person FOREIGN KEY (accused_id) REFERENCES ACCUSED(person_id)
);

CREATE TABLE IF NOT EXISTS CASE_OFFICER (
	case_id VARCHAR(50) NOT NULL,
	officer_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (case_id, officer_id),
	CONSTRAINT fk_case_officer_case FOREIGN KEY (case_id) REFERENCES `CASE`(case_id),
	CONSTRAINT fk_case_officer_person FOREIGN KEY (officer_id) REFERENCES POLICE(person_id)
);

CREATE TABLE IF NOT EXISTS EVIDENCE (
	evidence_id VARCHAR(50) PRIMARY KEY,
	case_id VARCHAR(50) NOT NULL,
	description TEXT NOT NULL,
	type ENUM('DOCUMENT', 'PHYSICAL', 'DIGITAL', 'PHOTOGRAPH', 'VIDEO', 'OTHER') NOT NULL,
	collected_date DATE NOT NULL,
	collected_by VARCHAR(50) NOT NULL,
	location VARCHAR(255) NOT NULL,
	CONSTRAINT fk_evidence_case FOREIGN KEY (case_id) REFERENCES `CASE`(case_id),
	CONSTRAINT fk_evidence_police FOREIGN KEY (collected_by) REFERENCES POLICE(person_id)
);

-- Test Data

-- PERSON records
INSERT IGNORE INTO PERSON (person_id, name, age, phone, username, password, role) VALUES
('P001', 'Officer Rajesh', 35, '9876543210', 'rajesh', 'password123', 'POLICE'),
('P002', 'Officer Priya', 28, '9876543211', 'priya', 'password123', 'POLICE'),
('P003', 'Amit', 42, '9876543212', 'amit', 'password123', 'VICTIM'),
('P004', 'Neha', 31, '9876543213', 'neha', 'password123', 'VICTIM'),
('P005', 'Raja Ravi Verma', 38, '9876543214', 'ravi', 'password123', 'ACCUSED'),
('P006', 'Suresh', 45, '9876543215', 'suresh', 'password123', 'ACCUSED');

-- POLICE records
INSERT IGNORE INTO POLICE (person_id, `rank`, department, badge_number) VALUES
('P001', 'Inspector', 'Criminal Investigation Department', 'BADGE001'),
('P002', 'Sub-Inspector', 'Local Police Station', 'BADGE002');

-- VICTIM records
INSERT IGNORE INTO VICTIM (person_id, crime_details, emergency_contact) VALUES
('P003', 'House burglary on 15th Sept 2026', '9876543220'),
('P004', 'Vehicle theft on 10th Sept 2026', '9876543221');

-- ACCUSED records
INSERT IGNORE INTO ACCUSED (person_id, charges, bail_status) VALUES
('P005', 'Theft and burglary', FALSE),
('P006', 'Vehicle theft and fraud', TRUE);

-- CASE records
INSERT IGNORE INTO `CASE` (case_id, status, description, officer_username, victim_username, accused_username) VALUES
('CASE001', 'OPEN', 'Burglary at residential apartment', 'rajesh', 'amit', 'ravi'),
('CASE002', 'CLOSED', 'Theft of white sedan from parking lot', 'priya', 'neha', 'suresh'),
('CASE003', 'OPEN', 'Attempted robbery at jewelry shop', 'rajesh', 'amit', 'suresh');

-- CASE_OFFICER records
INSERT IGNORE INTO CASE_OFFICER (case_id, officer_id) VALUES
('CASE001', 'P001'),
('CASE002', 'P002'),
('CASE003', 'P001');

-- CASE_ACCUSED records
INSERT IGNORE INTO CASE_ACCUSED (case_id, accused_id) VALUES
('CASE001', 'P005'),
('CASE002', 'P006'),
('CASE003', 'P006');

-- EVIDENCE records
INSERT IGNORE INTO EVIDENCE (evidence_id, case_id, description, type, collected_date, collected_by, location) VALUES
('EVD001', 'CASE001', 'Fingerprints from broken window', 'PHYSICAL', '2024-03-16', 'P001', 'Crime Scene'),
('EVD002', 'CASE001', 'CCTV footage from nearby shop', 'VIDEO', '2024-03-16', 'P001', 'Nearby Electronics Store'),
('EVD003', 'CASE002', 'Parking lot security footage', 'VIDEO', '2024-03-21', 'P002', 'Residential Parking Area'),
('EVD004', 'CASE003', 'Witness statement document', 'DOCUMENT', '2024-03-25', 'P001', 'Jewelry Shop');

-- FIR records
INSERT IGNORE INTO FIR (fir_id, date, location, description, victim_id, registered_by) VALUES
('FIR001', '2024-03-15', 'Kozhikode, Kerala', 'House burglary reported by Amit', 'P003', 'P001'),
('FIR002', '2024-03-20', 'Kollam, Kerala', 'Vehicle theft reported by Neha', 'P004', 'P002'),
('FIR003', '2024-03-24', 'Thiruvananthapuram, Kerala', 'Attempted robbery at jewelry shop', 'P003', 'P001');
