# Asset Management System Service

## Project Description

Phase 2 mock project Asset Management System

### Technologies
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Data JPA** - For database operations and ORM
- **Spring Web** - For creating RESTful web services
- **Spring Validation** - For input validation
- **Maven** - For dependency management and building
- **PostgreSQL** - Database
- **Spring Security** - For securing REST APIs
- **JWT** - For authentication and authorization
- **Lombok** - For reducing boilerplate code
- **MapStruct** - For object mapping
- **Swagger** - For API documentation
- **JaCoCo** - For code coverage
- **Validation** - For input validation

### Project Structure
```
src/
├── main/
│   ├── java/com/rookie/asset_management/
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST API controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── mapper/         # Model mappers
│   │   ├── entity/         # JPA entity classes
│   │   ├── exception/      # Custom exceptions and handlers
│   │   ├── repository/     # Spring Data JPA repositories
│   │   ├── service/        # Business logic
│   └── resources/
│       ├── application.properties  # Application configuration
│       └── static/                 # Static resources
└── test/                           # Test cases
```

## Features
- **Asset Management**: Create, update, view, and delete assets
- **User Management**: Admin can manage system users
- **Assignment Management**: Assign and track assets to users
- **Return Management**: Process asset return requests
- **Authentication**: Secure login and role-based access control
- **Reporting**: Generate reports on asset utilization and status

## Setup and Installation

### Prerequisites
- Docker and Docker Compose

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/hoanglequocanhit89/AssetManagementService.git
   cd AssetManagementService
   ```
   
2. Configure environment variables, follow the `.env.example` file

3. Run docker compose to start the application and PostgreSQL database:
   ```bash
   docker compose up
   ```

## API Documentation
The API documentation is available at `/swagger-ui.html` when running the application.

## Contributing
1. Fork the project
2. Create your feature branch (`git checkout -b <prefix>/#<backlog-id>_<branch-name>`)
   - `<prefix>`: feat, fix, ref, hotfix, release
   - `<branch-name>`: A short description of the feature or bug
   - Example: `feat/#43_amazing-feature`
3. Commit your changes (`git commit -m '<type>: <description> by taskid <taskid>'`)
   - `<type>`: feat, fix, docs, style, refactor, test
   - `<description>`: A short description of the change
   - Example: `feat: add new asset management api`
4. Push to the branch (`git push origin feat/#43_amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details.