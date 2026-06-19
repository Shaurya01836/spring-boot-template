# Spring Boot Template

A production-ready Spring Boot starter template for building REST APIs with a clean layered structure, database support, migrations, health checks, and local frontend integration.

This template is designed to help teams start new services with a sensible default architecture instead of spending time on repetitive project setup.

## What This Template Gives You

- A standard Spring Boot application foundation with Java 21.
- Layered package structure for controllers, services, entities, DTOs, repositories, mappers, utilities, and configuration.
- PostgreSQL as the default database.
- Optional MySQL support through a dedicated profile.
- JPA and Flyway support for persistence and schema evolution.
- CORS configuration for common local frontend development ports.
- A health-check endpoint for basic service verification.
- Testcontainers-based integration test setup.

## Why This Template Exists

Most backend projects start with the same repetitive decisions:

- Where should controllers go?
- How should service logic be separated?
- Which database is the default?
- How should local frontend apps connect?
- How should schema changes be managed safely?

This template answers those questions up front so teams can move directly into business logic.

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator
- Flyway
- PostgreSQL
- MySQL
- Testcontainers
- Lombok

## Project Structure

```text
src/main/java/io/github/shaurya01836/template
├── controller
├── service
│   └── impl
├── entity
├── dto
├── repository
├── mapper
├── config
├── exception
├── util
└── constant
```

### Package Intent

- controller: REST endpoints and request handling.
- service: business logic contracts.
- service/impl: service implementations.
- entity: JPA entities mapped to the database.
- dto: request and response models.
- repository: database access layer.
- mapper: object mapping between entities and DTOs.
- config: application configuration such as CORS.
- exception: custom exceptions and global error handling.
- util: reusable helper methods.
- constant: application-wide constants.

## Database Support

### Default Database: PostgreSQL

PostgreSQL is the default database in [application.properties](src/main/resources/application.properties).

Use this when you want the template to behave exactly as shipped.

### Optional Database: MySQL

MySQL is available through [application-mysql.properties](src/main/resources/application-mysql.properties).

Activate it with:

```bash
spring.profiles.active=mysql
```

This keeps the default PostgreSQL setup intact while still giving teams the option to use MySQL when needed.

## Configuration Overview

### Default Configuration

The base configuration includes:

- application name
- PostgreSQL datasource settings
- Hibernate validation mode
- SQL logging
- PostgreSQL dialect
- Flyway toggle

### MySQL Profile

The MySQL profile includes:

- MySQL datasource URL
- MySQL driver class
- MySQL dialect
- JPA validation settings
- Flyway toggle

### CORS

The project includes a CORS configuration for common frontend development hosts:

- http://localhost:3000
- http://localhost:4200
- http://localhost:5173
- http://127.0.0.1:3000
- http://127.0.0.1:4200
- http://127.0.0.1:5173

This is useful for React, Angular, and Vite-based frontend apps during local development.

## Health Check Endpoint

The template exposes a basic health endpoint:

```http
GET /v1/api/health-check
```

Response:

```text
server is running
```

This is a simple service readiness check and also a good starting point for more advanced health endpoints later.

## Running the Application

### Prerequisites

- Java 21
- Maven Wrapper or Maven installed
- PostgreSQL if using the default profile
- MySQL if using the MySQL profile

### Start the App

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### Run with MySQL Profile

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

Or set:

```properties
spring.profiles.active=mysql
```

## Build and Test

### Compile

```powershell
.\mvnw.cmd -DskipTests compile
```

### Run Tests

```powershell
.\mvnw.cmd test
```

The project includes Testcontainers support for integration testing.

## Flyway and Schema Management

Flyway is included for managing database migrations in a controlled way.

Recommended migration location:

```text
src/main/resources/db/migration
```

Typical naming pattern:

```text
V1__create_users_table.sql
V2__add_email_column.sql
```

Keep migrations small, ordered, and reversible where possible.

## Recommended Development Workflow

1. Create a DTO for the API contract.
2. Add the entity and repository.
3. Implement service logic in the service layer.
4. Expose the feature through a controller.
5. Add a Flyway migration for the schema.
6. Write integration tests for the API and persistence path.

## Production Notes

- Keep `ddl-auto=validate` in production so Hibernate validates the schema instead of modifying it.
- Use Flyway for all schema changes.
- Store credentials in environment variables or a secrets manager.
- Use profile-specific config files for environment differences.
- Keep CORS restricted to known frontend origins.

## Suggested Next Additions

If you want to extend this template further, the next practical additions are:

- a global exception handler
- standardized API response wrapper
- security with JWT or session auth
- OpenAPI/Swagger documentation
- application-dev.properties and application-prod.properties
- a sample entity, repository, service, and controller flow

## License

Add your project license here if this template will be shared publicly.