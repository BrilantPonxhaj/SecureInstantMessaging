create database secure_chat;

use database secure_chat;

CREATE TABLE Messages(
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(255) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    time_stamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    message_hash TEXT NOT NULL
);