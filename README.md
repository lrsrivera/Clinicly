# Clinicly - School Clinic Management System

A comprehensive JavaFX-based clinic management system for Immaculada Concepcion College, built with Spring Boot and SQLite.

## Features

- **Patient Management**: Add, edit, and manage patient records
- **Appointment Scheduling**: Schedule and track medical appointments
- **User Authentication**: Secure login system with role-based access
- **Data Import/Export**: Excel template support for bulk patient import
- **Modern UI**: Clean JavaFX interface with responsive design

## Technology Stack

- **Frontend**: JavaFX 17
- **Backend**: Spring Boot 2.7.0
- **Database**: SQLite
- **Build Tool**: Maven
- **Java Version**: 11+

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- SQLite (included with the application)

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd Clinicly
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

## Default Login Credentials

- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `admin`

## Project Structure

```
src/
├── main/
│   ├── java/com/icc/clinic/
│   │   ├── ClinicApplication.java          # Main application class
│   │   ├── config/                         # Configuration classes
│   │   ├── controller/                     # JavaFX controllers
│   │   ├── model/                          # Data models
│   │   ├── repository/                     # Data access layer
│   │   └── service/                        # Business logic
│   └── resources/
│       ├── fxml/                           # JavaFX UI files
│       ├── application.properties          # Application configuration
│       ├── style.css                       # UI styling
│       └── *.png, *.gif                   # Images and icons
├── data/
│   └── clinic.db                          # SQLite database
└── Patient_Import_Template.xlsx           # Excel template for patient import
```

## Database

The application uses SQLite database located at `data/clinic.db`. The database will be automatically created on first run with the following tables:

- `users` - User accounts and authentication
- `patients` - Patient records and medical information
- `appointments` - Appointment scheduling and tracking

## Usage

1. **Login**: Use the default admin credentials to access the system
2. **Patient Management**: Add new patients or import from Excel template
3. **Appointments**: Schedule and manage medical appointments
4. **Data Export**: Export patient data for reporting

## Development

### Building from Source
```bash
mvn clean package
```

### Running Tests
```bash
mvn test
```

### Database Reset
To reset the database, delete `data/clinic.db` and restart the application. The system will recreate the database with default admin user.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is developed for educational purposes at Immaculada Concepcion College.

## Support

For technical support or questions, please contact the development team.
