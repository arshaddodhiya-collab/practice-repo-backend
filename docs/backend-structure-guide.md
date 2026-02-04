# Backend Structure & Best Practices Guide

This document outlines the architectural patterns, directory structure, and coding standards used in this backend project. Use this as a reference when creating new backend services to ensure consistency, maintainability, and scalability.

## 1. Technology Stack

- **Language**: Java 17+
- **Framework**: Spring Boot 3+
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **ORM**: Hibernate / Spring Data JPA

## 2. Directory Structure

The project follows a standard layered architecture. All source code is located under `src/main/java/com/<organization>/<project>`.

```text
com.test.practice
├── controller       # REST Controllers (API Layer)
├── service          # Business Logic Layer
├── repository       # Data Access Layer (Interfaces)
├── entity           # Database Entities (JPA)
├── dto              # Data Transfer Objects (Request/Response)
├── exception        # Global Exception Handling
└── PracticeApplication.java # Entry Point
```

## 3. Architectural Layers

### 3.1. Controller Layer (`controller` package)
- **Responsibility**: Handle HTTP requests, validate input, and call services.
- **Naming**: `[Resource]Controller` (e.g., `UserController`).
- **Annotations**: `@RestController`, `@RequestMapping("/resource")`.
- **Best Practices**:
    - Use **Constructor Injection** for dependencies.
    - Accept and return **DTOs**, never Entities.
    - Use `@Valid` to trigger Bean Validation on RequestBodies.
    - Return `ResponseEntity<DTO>` for clear HTTP status codes.

```java
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }
}
```

### 3.2. Service Layer (`service` package)
- **Responsibility**: Contains business logic, transaction management, and mapping between Entities and DTOs.
- **Naming**: `[Resource]Service` (e.g., `UserService`).
- **Annotations**: `@Service`.
- **Best Practices**:
    - Use `@Transactional` for methods that modify data.
    - Perform manual mapping or use a library (Mapper) to convert Entity <-> DTO.
    - Avoid returning Entities to the Controller.
    - Use `Objects.requireNonNull` for defensive coding.

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDTO createUser(UserDTO dto) {
        // Map DTO to Entity, Save, Map back to DTO
    }
}
```

### 3.3. Repository Layer (`repository` package)
- **Responsibility**: Interface with the database.
- **Naming**: `[Resource]Repository` (e.g., `UserRepository`).
- **Annotations**: `@Repository`.
- **Best Practices**:
    - Extend `JpaRepository<Entity, ID>`.
    - detailed query methods can be defined using method naming conventions or `@Query`.

### 3.4. Entity Layer (`entity` package)
- **Responsibility**: Represents database tables.
- **Annotations**: `@Entity`, `@Table`.
- **Best Practices**:
    - Use `Long` for primary keys (`@Id`, `@GeneratedValue`).
    - Use Bean Validation annotations (`@NotBlank`, `@Email`) on fields.
    - Define relationships (`@OneToMany`, `@ManyToOne`) carefully.
    - Default constructor is required by JPA.

### 3.5. DTO Layer (`dto` package)
- **Responsibility**: Carry data between processes. Decouples API contract from Database Schema.
- **Naming**: `[Resource]DTO`.
- **Best Practices**:
    - Use basic data types.
    - Include Validation annotations (`@NotBlank`, etc.) to be checked in Controller.
    - Separate Request and Response DTOs if the data shapes diverge significantly.

## 4. Exceptional Handling

- **Global Handler**: Use a `@RestControllerAdvice` class (`GlobalExceptionHandler`) to handle exceptions globally.
- **Custom Exceptions**: Create specific runtime exceptions (e.g., `ResourceNotFoundException`).
- **Response**: Return a standardized `ErrorResponse` object (Timestamp, Status, ErrorType, Message).

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        // Build and return error response with 404 status
    }
}
```

## 5. Configuration & Best Practices

### 5.1. Database Configuration
- **Manual Migration**: We use **manual SQL scripts** for schema changes.
- **Properties**:
    - `spring.jpa.hibernate.ddl-auto=validate`: Prevents Hibernate from automatically altering the schema. This ensures production stability.
    - `spring.jpa.open-in-view=false`: Disables OSIV to prevent database connection leaks and ensure all data loading happens within the Service transaction.

```properties
# application.properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
```

### 5.2. Migration Workflow
1. Write the SQL change (CREATE TABLE, ALTER TABLE) in `db/manual_migration.sql`.
2. Apply the SQL manually to your local database.
3. Update the `Entity` class to match the new schema.
4. Run the application; Hibernate will validate the Entity against the DB schema.

### 5.3. Dependency Injection
- Always use **Constructor Injection** instead of `@Autowired` on fields. This makes testing easier and ensures immutability.

### 5.4. Validation
- Validate inputs at the controller level using Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Size`).
- Fail fast with meaningful error messages.
