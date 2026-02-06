# User Creation Flow (Controller -> Service -> Repository)

This document explains the flow of data when a `POST /api/users` request is made.

## The Sequence Diagram

```mermaid
sequenceDiagram
    participant C as UserController
    participant S as UserService
    participant R as UserRepository
    participant DB as Database
    
    Note over C: 1. Receive JSON request<br/>{ "name": "Artem" }
    
    C->>S: createUser(UserRequestDTO)
    
    Note over S: 2. Convert DTO to Entity<br/>User user = new User();<br/>user.setName(...);
    
    S->>R: save(user)
    
    R->>DB: INSERT INTO users (name) VALUES (?)
    DB-->>R: Return Saved Row (ID: 1)
    
    R-->>S: Return Saved User Entity
    
    Note over S: 3. Convert Entity to DTO<br/>new UserResponseDTO(1, "Artem")
    
    S-->>C: Return UserResponseDTO
    
    C-->>Client: 4. Return HTTP 201 Created<br/>{ "id": 1, "name": "Artem" }
```

## Step-by-Step Code Analysis

### 1. The Controller Layer ([UserController.java](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/controller/UserController.java))
**Role**: Handles the HTTP Request.
*   **Method**: [createUser(@RequestBody UserRequestDTO request)](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/controller/UserController.java#20-25)
*   **Action**: It receives the JSON body, converts it automatically into a `UserRequestDTO` Java object, and passes it to the Service.
    ```java
    UserResponseDTO response = userService.createUser(request);
    ```

### 2. The Service Layer ([UserService.java](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/service/UserService.java))
**Role**: Business Logic & Data Transformation.
*   **Method**: [createUser(UserRequestDTO request)](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/controller/UserController.java#20-25)
*   **Step A (DTO -> Entity)**: It creates a new [User](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/entity/User.java#6-30) entity and transfers data from the DTO.
    ```java
    User user = new User();
    user.setName(request.getName());
    ```
*   **Step B (Persistence)**: It calls the repository to save this entity.
    ```java
    User savedUser = userRepo.save(user);
    ```
*   **Step C (Entity -> DTO)**: After saving (which generates the ID), it converts the saved entity back into a response DTO.
    ```java
    return new UserResponseDTO(savedUser.getId(), savedUser.getName());
    ```

### 3. The Repository Layer ([UserRepository.java](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/repository/UserRepository.java))
**Role**: Database Communication.
*   **Method**: `save(entity)` (Inherited from `JpaRepository`)
*   **Action**: Hibernate generates the SQL `INSERT` statement and executes it on the database.
