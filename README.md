# Clinicly - School Clinic Management System

A comprehensive JavaFX-based clinic management system built with Spring Boot and SQLite, featuring role-based access control, data encryption, and secure password management.

## 🚀 Quick Start

### Download & Run
1. **Download**: Click the green "Code" button → "Download ZIP"
2. **Extract**: Unzip the file to your desired location
3. **Open Terminal**: Navigate to the `Clinicly-main` folder
4. **Create Data Directory**: `mkdir data` (if it doesn't exist)
5. **Run**: `mvn javafx:run`

### Default Login Credentials
- **IT Admin**: `it_admin` / `admin123` (Must change password on first login)
- **Nurse**: `nurse` / `nurse123` (Must change password on first login)

## 📋 Requirements

### System Requirements
- **Java**: Version 11 or higher
- **Maven**: Version 3.6 or higher
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 2GB RAM
- **Storage**: 100MB free space

### Required Software
1. **Java Development Kit (JDK) 11+**
   - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
   - Verify installation: `java -version`

2. **Apache Maven**
   - Download from: [Maven Website](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **JavaFX SDK** (for IntelliJ IDEA users)
   - Download from: [JavaFX Downloads](https://openjfx.io/openjfx-docs/)
   - Extract to: `C:\javafx-sdk-17.0.16\` (Windows) or similar path

## 🛠️ How to Run

### Method 1: Command Line (Recommended)
```bash
# Navigate to project directory
cd Clinicly-main

# Run the application
mvn javafx:run
```

### Method 2: IntelliJ IDEA
1. **Open Project**: File → Open → Select `Clinicly-main` folder
2. **Wait for Maven Import**: Let IntelliJ download dependencies
3. **Configure Run Configuration**:
   - Run → Edit Configurations
   - Add New Configuration → Application
   - Main class: `com.icc.clinic.ClinicApplication`
   - Module: `clinic-management`
4. **Add VM Options**:
   ```
   --module-path "C:\javafx-sdk-17.0.16\lib" --add-modules javafx.controls,javafx.fxml
   ```
   *(Replace path with your JavaFX SDK location)*
5. **Run**: Click the green play button

### Method 3: Eclipse
1. **Import Project**: File → Import → Maven → Existing Maven Projects
2. **Select Folder**: Choose `Clinicly-main` directory
3. **Run as Java Application**: Right-click `ClinicApplication.java` → Run As → Java Application

## 🔧 Troubleshooting

### Common Issues

**Issue**: `path to 'data/clinic.db': 'data' does not exist`
- **Solution**: Create the data directory manually:
  ```bash
  mkdir data
  mvn javafx:run
  ```

**Issue**: `JavaFX runtime components are missing`
- **Solution**: Use `mvn javafx:run` instead of `mvn spring-boot:run`

**Issue**: `No plugin found for prefix 'javafx'`
- **Solution**: Make sure you're in the `Clinicly-main` directory, not the parent folder

**Issue**: IntelliJ shows "JavaFX runtime components are missing"
- **Solution**: Add VM options: `--module-path "C:\javafx-sdk-17.0.16\lib" --add-modules javafx.controls,javafx.fxml`

**Issue**: `Port 8080 is already in use`
- **Solution**: Kill existing Java processes:
  ```bash
  # Windows
  taskkill /F /IM java.exe
  
  # macOS/Linux
  pkill -f java
  ```

**Issue**: Application won't start
- **Solution**: 
  1. Check Java version: `java -version` (must be 11+)
  2. Check Maven version: `mvn -version` (must be 3.6+)
  3. Create data directory: `mkdir data`
  4. Delete `data/clinic.db` and restart if corrupted

## 🔐 Security Features

- **Role-Based Access Control**: IT Admin and Nurse roles with different permissions
- **Data Encryption**: Sensitive patient and user data encrypted at rest
- **Password Security**: Strong password requirements and forced password changes
- **Input Validation**: Comprehensive data validation and sanitization

## 📊 Features

- **Patient Management**: Add, edit, and manage patient records
- **Appointment Scheduling**: Schedule and track medical appointments
- **User Management**: Create and manage user accounts
- **Data Import/Export**: Excel template support for bulk patient import
- **Search & Filter**: Advanced search and filtering capabilities

## 📁 Project Structure

```
Clinicly-main/
├── src/main/java/com/icc/clinic/
│   ├── ClinicApplication.java              # Main application class
│   ├── config/                             # Spring configuration
│   ├── controller/                         # JavaFX UI controllers
│   ├── model/                              # Data models (User, Patient, etc.)
│   ├── repository/                         # Database access layer
│   ├── service/                            # Business logic layer
│   ├── util/                               # Utility classes
│   └── validation/                         # Input validation
├── src/main/resources/
│   ├── fxml/                               # JavaFX UI files
│   ├── application.properties              # App configuration
│   ├── style.css                           # UI styling
│   └── *.png, *.gif                       # Images and icons
├── data/
│   └── clinic.db                           # SQLite database (auto-created)
├── Patient_Import_Template.xlsx            # Excel import template
├── pom.xml                                 # Maven configuration
└── README.md                               # This file
```

## 🗄️ Database

The application uses SQLite database located at `data/clinic.db`. The database is automatically created on first run with these tables:

> **Note**: If you get an error about the `data` directory not existing, create it manually with `mkdir data` before running the application.

- **`users`** - User accounts and authentication
- **`patients`** - Patient records and medical information  
- **`appointments`** - Appointment scheduling and tracking
- **`roles`** - User roles (IT, Nurse)
- **`permissions`** - System permissions
- **`password_audit`** - Password change history

## 🎯 Usage Guide

### First Time Setup
1. **Download & Extract**: Download ZIP and extract to desired location
2. **Install Requirements**: Install Java 11+ and Maven 3.6+
3. **Create Data Directory**: Run `mkdir data` in the `Clinicly-main` folder
4. **Run Application**: Use `mvn javafx:run` in the `Clinicly-main` folder
5. **Login**: Use default credentials (must change password on first login)

### Daily Usage
1. **Login**: Use your credentials to access the system
2. **Patient Management**: Add patients manually or import from Excel
3. **Appointments**: Schedule and manage medical appointments
4. **User Management**: (IT Admin only) Manage user accounts
5. **Data Export**: Export patient data for reporting

### Excel Import
1. Download the `Patient_Import_Template.xlsx`
2. Fill in patient data following the template format
3. Use the "Import Patients" feature in the application
4. Verify imported data in the Patients List

## 🔧 Development

### Building from Source
```bash
# Clean and compile
mvn clean compile

# Create JAR file
mvn clean package

# Run tests
mvn test
```

### Database Management
```bash
# Reset database (deletes all data)
rm data/clinic.db
mvn javafx:run

# View database (optional)
sqlite3 data/clinic.db
```

### IDE Setup
- **IntelliJ IDEA**: Import as Maven project, add JavaFX VM options
- **Eclipse**: Import as Maven project, run as Java Application
- **VS Code**: Install Java Extension Pack, open folder

## 🚨 Important Notes

### Security
- **Change Default Passwords**: Both IT Admin and Nurse must change passwords on first login
- **Strong Passwords**: New passwords must contain uppercase, lowercase, numbers, and special characters
- **Data Encryption**: Sensitive data is encrypted in the database
- **Role Permissions**: IT Admin has full access, Nurse has limited clinical access

### Troubleshooting
- **Port 8080 in use**: Kill existing Java processes with `taskkill /F /IM java.exe`
- **Database locked**: Close application and restart
- **Import errors**: Check Excel template format and required fields

## 📞 Support

For technical support or questions:
- Check the troubleshooting section above
- Review the project documentation
- Contact the development team

## 📄 License

This project is developed for educational purposes.

---

**Happy Clinic Management! 🏥**

