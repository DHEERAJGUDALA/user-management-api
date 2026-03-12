# User Management REST API

A production-quality REST API built with Spring Boot 4 for managing users. Demonstrates core backend engineering patterns including DTO layer, global exception handling, bean validation, JPA persistence, and API documentation.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.3 |
| Spring Data JPA | 4.0.3 |
| Hibernate | 7.2.4 |
| PostgreSQL | 18.3 |
| Lombok | 1.18.42 |
| SpringDoc OpenAPI (Swagger) | 2.8.6 |
| Maven | 3.x |

---

## Architecture

```
src/main/java/com/usermanagement/
├── controller/
│   └── UserController.java          # HTTP layer — request mapping, status codes
├── service/
│   ├── UserService.java             # Service interface
│   └── UserServiceImpl.java         # Business logic, transactions
├── repository/
│   └── UserRepository.java          # Data access layer
├── entity/
│   └── User.java                    # JPA entity — maps to users table
├── dto/
│   ├── CreateUserRequest.java       # POST request body with validation
│   ├── UpdateUserRequest.java       # PUT request body with validation
│   └── UserResponse.java            # Response DTO (no password exposed)
└── exception/
    ├── UserNotFoundException.java
    ├── EmailAlreadyExistsException.java
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java  # @RestControllerAdvice — centralized error handling
```

---

## Prerequisites

- JDK 21+
- PostgreSQL running on `localhost:5432`
- Maven 3.x

---

## Setup

**1. Create the database**
```sql
CREATE DATABASE user_management_db;
```

**2. Configure credentials**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=postgres
spring.datasource.password=your_password
```

**3. Run the application**
```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8082`. Hibernate auto-creates the `users` table on first run.

---

## API Endpoints

Base URL: `http://localhost:8082/api/v1`

| Method | Endpoint | Description | Status |
|---|---|---|---|
| POST | `/users` | Create a new user | 201 |
| GET | `/users` | Get all users | 200 |
| GET | `/users/{id}` | Get user by ID | 200 |
| GET | `/users/email/{email}` | Get user by email | 200 |
| PUT | `/users/{id}` | Update user | 200 |
| DELETE | `/users/{id}` | Soft delete user | 204 |

---

## Request & Response Examples

**Create User**
```http
POST /api/v1/users
Content-Type: application/json

{
  "firstName": "Sai",
  "lastName": "Sharanam",
  "email": "sai@example.com",
  "password": "password123",
  "role": "USER"
}
```

```json
// 201 Created
{
  "id": 1,
  "firstName": "Sai",
  "lastName": "Sharanam",
  "email": "sai@example.com",
  "role": "USER",
  "active": true,
  "createdAt": "2026-03-12T16:02:48.029727",
  "updatedAt": "2026-03-12T16:02:48.029727"
}
```

**Validation Error**
```json
// 400 Bad Request
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-03-12T16:06:34.988",
  "errors": [
    "First name is required",
    "Email must be valid",
    "Password must be at least 8 characters"
  ]
}
```

**User Not Found**
```json
// 404 Not Found
{
  "status": 404,
  "message": "User not found with id: 999",
  "timestamp": "2026-03-12T16:07:01.315",
  "errors": null
}
```

---

## User Roles

| Role | Description |
|---|---|
| `USER` | Standard user |
| `ADMIN` | Administrator |
| `MODERATOR` | Moderator |

---

## Key Design Decisions

- **DTO pattern** — Entity never exposed directly to API. `UserResponse` never includes `password`.
- **Soft deletes** — `DELETE` sets `active = false`. User records are never hard deleted.
- **Global exception handler** — `@RestControllerAdvice` centralizes all error formatting. Controllers stay clean.
- **Service interface + impl** — Programming to interface for testability and swappability.
- **`@Transactional(readOnly = true)`** — All read methods marked read-only for Hibernate dirty-checking optimization.
- **API versioning** — All routes prefixed with `/api/v1` for future-proofing.

---

## API Documentation

Swagger UI available at:
```
http://localhost:8082/swagger-ui/index.html
```

OpenAPI JSON spec:
```
http://localhost:8082/v3/api-docs
```

---

## Actuator Endpoints

| Endpoint | Description |
|---|---|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Metrics |

---

## Notes

- Password is stored as plain text — BCrypt hashing will be added in the Spring Security phase.
- `spring.jpa.hibernate.ddl-auto=update` is used for development only. Production should use Flyway or Liquibase for schema migrations.
