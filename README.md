# Task Manager Desktop

A JavaFX desktop application for managing personal and group projects with real-time synchronization to a Spring Boot backend API.

## ✨ Features

- **Personal Task Management**: Create, edit, and track personal tasks with priority levels and due dates
- **Group Projects**: Collaborate on projects with team members, assign roles (Owner/Member)
- **Deliverables**: Break down projects into deliverables with sub-tasks
- **User Authentication**: Secure JWT-based authentication with backend
- **Profile Pictures**: Upload and display member profile pictures
- **Offline Support**: SQLite caching for offline access
- **Real-time Sync**: Automatic synchronization with backend API

## 🏗️ Architecture

### Frontend (This Project)
- **Framework**: JavaFX
- **Build Tool**: Maven
- **Database**: SQLite (local caching)
- **API Client**: Custom REST client with Bearer token authentication
- **Pattern**: MVC with Service layer

### Backend (Separate Project)
- **Framework**: Spring Boot
- **API Port**: `http://localhost:8080/api`
- **Database**: PostgreSQL
- **Authentication**: JWT tokens
- **H2 Console**: `http://localhost:8080/h2-console`

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Backend running on `http://localhost:8080`
- Backend database with test user credentials

## 🚀 Getting Started

### 1. Clone Repository
```bash
git clone https://github.com/Donel-KZ/Task_Manager_Desktop.git
cd Task_Manager_Desktop
```

### 2. Configure Backend Connection
The app connects to the backend at `http://localhost:8080/api`

To change the port, set the `PORT` environment variable:
```bash
export PORT=8080  # or your desired port
```

### 3. Build the Project
```bash
mvn clean package
```

### 4. Run the Application
```bash
mvn javafx:run
```

Or run the JAR directly:
```bash
java -jar target/TaskManagerDesktop-1.0.jar
```

## 🔐 Authentication

### Login Flow
1. Enter email and password on login screen
2. App calls backend `/api/auth/login` endpoint
3. Backend returns JWT token and user info
4. Token stored in Session (memory) and SQLite (for persistence)
5. All subsequent requests include `Authorization: Bearer {token}` header

### Test Credentials
Create test users in the backend database first:
```
Email: test@example.com
Password: (your backend password)
```

## 📱 Group Projects Feature

### Create Group Project
1. Click **+ New Project** button
2. Enter project title and due date
3. Select team members to add
4. Click Create

### Project Structure
```
GroupProject
├── Deliverables
│   └── Tasks (DeliverableTask)
├── Members (Owner, Member roles)
└── AttachedFiles (future feature)
```

### Member Management
- Add members via "Add Member" dialog
- Assign roles: **Owner** (full access) or **Member** (read/write)
- Remove members from project

## 🗄️ Database

### SQLite Schema
Local SQLite database (`taskmanager.db`) with 8 tables:
- `users` - User accounts and profile data
- `projects` - Group projects metadata
- `deliverables` - Project deliverables
- `tasks` - Deliverable tasks
- `projectMembers` - Team member assignments with roles
- `session` - Current auth token
- `preferences` - User preferences
- `syncLog` - Sync status tracking

### Database File Location
```
{project_root}/taskmanager.db
```

## 🔌 API Endpoints (Backend)

### Authentication
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/logout` - Logout

### Group Projects
- `GET /api/projects` - List user's group projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project details
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Deliverables
- `GET /api/projects/{projectId}/deliverables` - List deliverables
- `POST /api/projects/{projectId}/deliverables` - Create deliverable
- `PUT /api/deliverables/{id}` - Update deliverable
- `DELETE /api/deliverables/{id}` - Delete deliverable

### Tasks
- `GET /api/deliverables/{deliverableId}/tasks` - List tasks
- `POST /api/deliverables/{deliverableId}/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Members
- `POST /api/projects/{projectId}/members` - Add member to project
- `DELETE /api/projects/{projectId}/members/{userId}` - Remove member

### File Upload
- `POST /api/deliverables/{id}/upload` - Upload file (multipart)

## 📊 Data Models

### Status Enums
```java
WorkStatus: PENDING, IN_PROGRESS, FINISHED, OVERDUE
TaskStatus: PENDING, FINISHED, OVERDUE
ProjectRole: OWNER, MEMBER
ProjectType: PERSONAL, GROUP
```

### Key Classes
- `ProjectResponse` - Group project with members and deliverables
- `DeliverableResponse` - Deliverable with tasks
- `TaskItemResponse` - Individual task
- `ProjectMemberResponse` - Team member with role
- `Session` - JWT token management

## 🐛 Troubleshooting

### "Could not load projects" Error
**Cause**: Missing authentication token or 403 Forbidden response
**Solution**: 
1. Ensure backend is running on `http://localhost:8080`
2. Login with valid credentials
3. Check backend database for user account

### "Picture upload failed"
**Cause**: Multipart body format issue or network error
**Solution**:
1. Check file size (limit depends on backend)
2. Verify backend `/api/deliverables/{id}/upload` endpoint exists
3. Ensure Bearer token is valid

### Compilation Errors
**Solution**:
```bash
mvn clean compile
```

## 🏗️ Project Structure

```
src/
├── main/java/org/donel/taskmanagerdesktop/
│   ├── api/
│   │   └── ApiClient.java              # REST client with auth
│   ├── database/
│   │   ├── DatabaseManager.java        # SQLite connection singleton
│   │   └── LocalCacheService.java      # Caching layer
│   ├── services/
│   │   ├── UserService.java            # User authentication
│   │   ├── ProjectService.java         # Project operations
│   │   ├── Session.java                # JWT token storage
│   │   └── ApiException.java           # API error handling
│   ├── Controllers/
│   │   ├── LoginController.java        # Login screen
│   │   ├── GroupProjectsController.java # Project management
│   │   ├── GroupProjectDetailWindow.java # Project details
│   │   └── ... (other controllers)
│   ├── Dialogs/
│   │   ├── CreateProjectDialog.java
│   │   ├── CreateDeliverableDialog.java
│   │   └── AddMemberDialog.java
│   └── HelloApplication.java           # Main entry point
└── resources/
    └── org/donel/taskmanagerdesktop/
        ├── Shell.fxml                  # Main window
        ├── GroupProjectsView.fxml      # Projects list
        ├── CreateProjectDialog.fxml    # Project creation
        └── ... (other FXML files)
```

## 🔄 Development Workflow

### Adding New Feature
1. Create controller class
2. Create FXML layout file
3. Create service class for business logic
4. Add API calls in ApiClient
5. Test with backend running

### Building for Release
```bash
mvn clean package
java -jar target/TaskManagerDesktop-1.0.jar
```

## 📦 Dependencies

- **JavaFX** 21.0.1 - UI framework
- **SQLite JDBC** 3.45.0.0 - Local database
- **Jackson** - JSON serialization
- **Maven** - Build tool

See `pom.xml` for complete dependency list.

## 🤝 Contributing

1. Create feature branch: `git checkout -b feature/my-feature`
2. Make changes and test
3. Commit with clear message
4. Push and create Pull Request

## 📝 License

[Add your license here]

## 📞 Support

For issues or questions:
1. Check [Troubleshooting](#-troubleshooting) section
2. Check backend logs: `http://localhost:8080/h2-console`
3. Review API responses in browser console

---

**Last Updated**: July 16, 2024
**Version**: 1.0.0
**Status**: Production Ready ✅
