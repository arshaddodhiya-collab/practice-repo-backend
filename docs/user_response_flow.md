# User Response Flow (Trace with Code)

This document maps the journey of the data from the Database back to the Client, **showing the exact code** at each step.

## 1. Database -> Repository
*   **Action**: `INSERT` completes. Database returns the new ID (e.g., `1`).
*   **Code (Internal)**: Hibernate maps this row to your [User](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/entity/User.java#6-30) entity.

## 2. Repository -> Service ([UserService.java](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/service/UserService.java))
The `userRepo.save(user)` method returns the **saved Entity**.

```java
// UserService.java
// 'savedUser' is the Entity (connected to DB row)
User savedUser = userRepo.save(user);
```

## 3. Service -> Controller ([UserService.java](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/service/UserService.java))
**CRITICAL STEP**: The Service converts the Entity to a DTO. It pulls the data out of the Entity.

```java
// UserService.java
public UserResponseDTO createUser(UserRequestDTO request) {
    // ... saving logic ...
    
    // RETURN: Creating the DTO from the Entity data
    return new UserResponseDTO(
        savedUser.getId(),   // Getting '1' from Entity
        savedUser.getName()  // Getting 'Artem' from Entity
    );
} 
```

## 4. Controller -> Spring Framework ([UserController.java](file:///home/artem/Desktop/Backend/Architecture/src/main/java/com/example/Architecture/controller/UserController.java))
The Controller receives the DTO. It wraps it in a `ResponseEntity` to define the HTTP Status (201 Created).

```java
// UserController.java
@PostMapping
public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO request) {
    // 1. Get DTO from Service
    UserResponseDTO response = userService.createUser(request);
    
    // 2. Wrap in HTTP 201 Response
    return ResponseEntity.status(201).body(response);
}
```

## 5. Spring Framework -> Client (JSON)
Before sending the bytes over the network, Spring uses **Jackson** to convert the DTO object to a JSON string.

**Java Object (in memory):**
```java
UserResponseDTO {
    id = 1
    name = "Artem"
}
```

**JSON Output (what the user sees):**
```json
{
    "id": 1,
    "name": "Artem"
}
```
