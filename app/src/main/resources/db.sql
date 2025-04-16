CREATE DATABASE IF NOT EXISTS PlagDetect;
USE PlagDetect;


CREATE TABLE IF NOT EXISTS uploaded_files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255),
    format varchar(50),
    validity enum('valid', 'invalid')
);

