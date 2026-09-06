CREATE DATABASE IF NOT EXISTS PoliceDB;
USE PoliceDB;

CREATE TABLE IF NOT EXISTS PERSON (
	person_id INT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	age INT NOT NULL,
	phone VARCHAR(15) NOT NULL,
	username VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	role ENUM('POLICE', 'VICTIM', 'ACCUSED') NOT NULL
);

CREATE TABLE IF NOT EXISTS POLICE (
	person_id INT PRIMARY KEY,
	`rank` VARCHAR(50) NOT NULL,
	department VARCHAR(100) NOT NULL,
	badge_number VARCHAR(50) NOT NULL UNIQUE,
	CONSTRAINT fk_police_person FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);

CREATE TABLE IF NOT EXISTS VICTIM (
	person_id INT PRIMARY KEY,
	address VARCHAR(255) NOT NULL,
	CONSTRAINT fk_victim_person FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);

CREATE TABLE IF NOT EXISTS ACCUSED (
	person_id INT PRIMARY KEY,
	accused_id INT NOT NULL UNIQUE,
	status ENUM('CONVICTED', 'NOT CONVICTED') NOT NULL,
	CONSTRAINT fk_accused_person FOREIGN KEY (person_id) REFERENCES PERSON(person_id)
);

CREATE TABLE IF NOT EXISTS FIR (
	fir_id INT PRIMARY KEY,
	date DATE NOT NULL,
	location VARCHAR(255) NOT NULL,
	description TEXT NOT NULL,
	victim_id INT NOT NULL,
	registered_by INT NOT NULL,
	CONSTRAINT fk_fir_victim FOREIGN KEY (victim_id) REFERENCES VICTIM(person_id),
	CONSTRAINT fk_fir_police FOREIGN KEY (registered_by) REFERENCES POLICE(person_id)
);

CREATE TABLE IF NOT EXISTS `CASE` (
	case_id INT PRIMARY KEY,
	fir_id INT NOT NULL,
	status ENUM('REGISTERED', 'UNDER_INVESTIGATION', 'CHARGESHEET_FILED', 'TRIAL_IN_PROGRESS', 'CLOSED') NOT NULL,
	victim_id INT NOT NULL,
	created_date DATE NOT NULL,
	description TEXT NOT NULL,
	CONSTRAINT fk_case_fir FOREIGN KEY (fir_id) REFERENCES FIR(fir_id),
	CONSTRAINT fk_case_victim FOREIGN KEY (victim_id) REFERENCES VICTIM(person_id)
);

CREATE TABLE IF NOT EXISTS CASE_ACCUSED (
	case_id INT NOT NULL,
	accused_id INT NOT NULL,
	PRIMARY KEY (case_id, accused_id),
	CONSTRAINT fk_case_accused_case FOREIGN KEY (case_id) REFERENCES `CASE`(case_id),
	CONSTRAINT fk_case_accused_person FOREIGN KEY (accused_id) REFERENCES ACCUSED(person_id)
);

CREATE TABLE IF NOT EXISTS CASE_OFFICER (
	case_id INT NOT NULL,
	officer_id INT NOT NULL,
	PRIMARY KEY (case_id, officer_id),
	CONSTRAINT fk_case_officer_case FOREIGN KEY (case_id) REFERENCES `CASE`(case_id),
	CONSTRAINT fk_case_officer_person FOREIGN KEY (officer_id) REFERENCES POLICE(person_id)
);

CREATE TABLE IF NOT EXISTS EVIDENCE (
	evidence_id INT PRIMARY KEY,
	case_id INT NOT NULL,
	description TEXT NOT NULL,
	type ENUM('DOCUMENT', 'PHYSICAL', 'DIGITAL', 'PHOTOGRAPH', 'VIDEO', 'OTHER') NOT NULL,
	collected_date DATE NOT NULL,
	collected_by INT NOT NULL,
	location VARCHAR(255) NOT NULL,
	CONSTRAINT fk_evidence_case FOREIGN KEY (case_id) REFERENCES `CASE`(case_id),
	CONSTRAINT fk_evidence_police FOREIGN KEY (collected_by) REFERENCES POLICE(person_id)
);
