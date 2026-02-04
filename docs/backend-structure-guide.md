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

## 5. Database Management & Migrations

The project uses a **manual migration strategy** to ensure full control over the database schema. Automations like `hibernate.ddl-auto` are set to `validate` to prevent accidental schema changes in production.

### 5.1. The `/db` Directory
Located at the project root, the `/db` directory contains all SQL scripts required to set up and update the database.

```text
/db
├── manual_migration.sql   # The master script containing all DDL statements
└── schema_versions/       # (Optional) Archived scripts for version history
    ├── v1_init.sql
    └── v2_add_columns.sql
```

### 5.2. Migration Workflow

#### Step 1: Draft the Change
Write your `CREATE`, `ALTER`, or `DROP` statements in `db/manual_migration.sql`. Always append new changes to the end of the file or creating a new versioned file if using a versioning system.

#### Step 2: Apply to Local Database
Run the SQL script against your local MySQL instance. You can use the command line or a GUI tool (Workbench, DBeaver).

**Command Line:**
```bash
mysql -u [username] -p[password] [database_name] < db/manual_migration.sql
```
*Example:*
```bash
mysql -u root -p practice_db < db/manual_migration.sql
```

#### Step 3: Update Java Entities
Modify the corresponding JPA Entity class in `src/main/java/.../entity` to match the new schema.
- Add/remove fields.
- Update `@Column` definitions.
- Update relationships (`@OneToMany`, etc.).

#### Step 4: Validate
Start the Spring Boot application.
- If `spring.jpa.hibernate.ddl-auto=validate` is set, the app will **fail to start** if your Entity does not match the Database table.
- This guarantees that your code and database are always in sync.

### 5.3. Managing Migrations in Teams
- **Single File Approach**: For smaller teams, keeping `manual_migration.sql` as the source of truth works well. Developer appends changes.
- **Versioned Files**: For larger teams, use numbered files (e.g., `V1__init.sql`, `V2__feature_post.sql`) and apply them strictly in order.
- **Review**: SQL scripts must be code-reviewed just like Java code.

## 6. Configuration & Best Practices

### 6.1. Properties Configuration
```properties
# application.properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
```

### 6.2. Dependency Injection
- Always use **Constructor Injection** instead of `@Autowired` on fields. This makes testing easier and ensures immutability.

### 6.3. Validation
- Validate inputs at the controller level using Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Size`).
- Fail fast with meaningful error messages.
