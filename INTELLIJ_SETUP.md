# IntelliJ IDEA Configuration for Clinicly RBAC Testing

## Project Setup

### 1. Import Project
1. Open IntelliJ IDEA
2. Select "Open" and navigate to `Clinicly-main` folder
3. Choose "Import project from external model" → "Maven"
4. Click "Next" and ensure "Import Maven projects automatically" is checked
5. Click "Finish"

### 2. Java Version Configuration
1. Go to `File` → `Project Structure` → `Project`
2. Set Project SDK to Java 11 or higher
3. Set Project language level to 11 or higher

### 3. Maven Configuration
1. Go to `File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
2. Ensure Maven home directory is set correctly
3. Set "User settings file" if needed

### 4. Run Configuration
1. Go to `Run` → `Edit Configurations`
2. Click "+" → `Application`
3. Configure:
   - **Name**: Clinicly Application
   - **Main class**: `com.icc.clinic.ClinicApplication`
   - **Module**: Select your project module
   - **VM options**: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`
   - **Program arguments**: (leave empty)

### 5. Database Configuration
The application uses SQLite database located at `data/clinic.db`. On first run, it will:
- Create the database automatically
- Initialize RBAC roles and permissions
- Create default users:
  - **IT Admin**: username=`it_admin`, password=`admin123`
  - **Nurse**: username=`nurse`, password=`nurse123`

## Testing RBAC Features

### Default Users Created:
1. **IT Admin** (`it_admin` / `admin123`)
   - Full system access
   - Can manage users
   - Can delete patients/appointments
   - Can access User Management tab

2. **Nurse** (`nurse` / `nurse123`)
   - Clinical data access only
   - Cannot delete patients/appointments
   - Cannot access User Management tab
   - Can manage patients and appointments

### Testing Steps:
1. **Login as IT Admin**:
   - Username: `it_admin`
   - Password: `admin123`
   - Verify: All tabs visible, can delete records, User Management accessible

2. **Login as Nurse**:
   - Username: `nurse`
   - Password: `nurse123`
   - Verify: User Management tab disabled, limited delete permissions

3. **Test User Management** (IT Admin only):
   - Create new users
   - Assign roles
   - Test permission restrictions

## Troubleshooting

### Common Issues:
1. **JavaFX not found**: Ensure JavaFX is in module path
2. **Database errors**: Delete `data/clinic.db` to reset
3. **Spring context issues**: Check Maven dependencies are resolved

### Debug Mode:
- Add `-Dspring.profiles.active=debug` to VM options
- Check console for RBAC initialization messages

## RBAC Implementation Summary

### Roles Created:
- **NURSE**: Clinical operations (patients, appointments, data import/export)
- **IT**: Full system access + user management + system configuration

### Permissions Implemented:
- Patient: CREATE, READ, UPDATE, DELETE, ARCHIVE
- Appointment: CREATE, READ, UPDATE, DELETE, ARCHIVE
- Data: IMPORT, EXPORT
- User: CREATE, READ, UPDATE, DELETE
- System: CONFIG, BACKUP, LOGS

### UI Changes:
- User Management tab (IT only)
- Role-based button visibility
- Permission-based action restrictions
- Dynamic UI updates based on user role
