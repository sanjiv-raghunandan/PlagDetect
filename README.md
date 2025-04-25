# PlagDetect

PlagDetect is a plagiarism detection system designed to analyze source code submissions for similarities. It uses the JPlag library for detecting plagiarism and provides a user-friendly JavaFX-based graphical interface for managing files, running plagiarism detection, and viewing results.

---

## Features

- **Plagiarism Detection**: Detect similarities in source code submissions using the JPlag library.
- **Google Drive Integration**: Download source code files from a specified Google Drive folder.
- **File Management**: Upload, view, and delete source code files with metadata extraction and validation.
- **Database Synchronization**: Sync the database with the local directory to ensure consistency.
- **JavaFX UI**: Intuitive graphical interface for managing files and running plagiarism detection.
- **Report Generation**: Generate and save detailed plagiarism detection reports.

---

## Prerequisites

Before running the project, ensure you have the following installed:

1. **Java Development Kit (JDK)**: Version 21 or higher.
2. **Gradle**: Add Gradle to your system's PATH.
3. **MySQL Database**: Ensure MySQL is installed and running.
4. **Google Drive API Credentials**: Place your `credentials.json` file in the `src/main/resources` directory.

---

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd PlagDetect
```

### 2. Install Gradle and Add to Path
Install Gradle and add it to your system's PATH. Install the necessary VSCode extensions as well.

### 3. Update Configuration
Update the password in the `config.properties` file.

### 4. Build and Run
Run the following commands:
```bash
gradle build
gradle run
```
(For Windows. Refer to the documentation for Linux equivalent.)

---

### 5.Configure the Database
- Create a MySQL database named PlagDetect.
- Use the SQL script provided in src/main/resources/db.sql to set up the required tables
- Update the database credentials in the config.properties file located in src/main/resources:
```bash
db.url=jdbc:mysql://localhost:3306/PlagDetect
db.user=<your-username>
db.password=<your-password>
```
Notes:
- This project uses JavaFX for UI implementation.
- Ensure the credentials.json file for Google Drive API is correctly placed in the src/main/resources directory.
- The submissions directory must exist in src/main/resources for file uploads.

