# API Flows and Diagrams

This document outlines the end-to-end execution flow for the key API endpoints in the system.

## 1. User Management

### 1.1 Create User
**Endpoint:** `POST /users`

**Flow:**
1.  Client sends `POST /users` with user details (JSON).
2.  `UserController` validates the Input (`@Valid`).
3.  `UserService` creates a new `User` entity.
4.  `UserRepository` saves the entity to the DB.
5.  `UserService` maps the saved entity to `UserDTO` and returns it.

```mermaid
sequenceDiagram
    participant Client
    participant UserController
    participant UserService
    participant UserRepository
    participant Database

    Client->>UserController: POST /users (UserDTO)
    UserController->>UserController: Validate Input (@Valid)
    UserController->>UserService: createUser(UserDTO)
    UserService->>UserService: Map DTO to User Entity
    UserService->>UserRepository: save(User)
    UserRepository->>Database: INSERT INTO users ...
    Database-->>UserRepository: Saved Entity
    UserRepository-->>UserService: Saved User
    UserService->>UserService: Map User to UserDTO
    UserService-->>UserController: UserDTO
    UserController-->>Client: 200 OK (UserDTO)
```

### 1.2 Get All Users
**Endpoint:** `GET /users`

```mermaid
sequenceDiagram
    participant Client
    participant UserController
    participant UserService
    participant UserRepository
    participant Database

    Client->>UserController: GET /users
    UserController->>UserService: getAllUsers()
    UserService->>UserRepository: findAll()
    UserRepository->>Database: SELECT * FROM users
    Database-->>UserRepository: List<User>
    UserRepository-->>UserService: List<User>
    UserService->>UserService: Stream & Map to UserDTOs
    UserService-->>UserController: List<UserDTO>
    UserController-->>Client: 200 OK (List<UserDTO>)
```

### 1.3 Get User by ID
**Endpoint:** `GET /users/{id}`

- Handles `ResourceNotFoundException` if user doesn't exist.

```mermaid
sequenceDiagram
    participant Client
    participant UserController
    participant UserService
    participant UserRepository
    participant Database

    Client->>UserController: GET /users/{id}
    UserController->>UserService: getUserById(id)
    UserService->>UserRepository: findById(id)
    UserRepository->>Database: SELECT * FROM users WHERE id = ?
    Database-->>UserRepository: Optional<User>
    
    alt User Found
        UserRepository-->>UserService: Optional.of(User)
        UserService->>UserService: Map to UserDTO
        UserService-->>UserController: UserDTO
        UserController-->>Client: 200 OK (UserDTO)
    else User Not Found
        UserRepository-->>UserService: Optional.empty()
        UserService-->>UserController: Throw ResourceNotFoundException
        UserController-->>Client: 404 Not Found (ErrorResponse)
    end
```

---

## 2. Post Management

### 2.1 Create Post
**Endpoint:** `POST /users/{userId}/posts`

- Ensures the User exists before creating a post.

```mermaid
sequenceDiagram
    participant Client
    participant PostController
    participant PostService
    participant UserRepository
    participant PostRepository
    participant Database

    Client->>PostController: POST /users/{userId}/posts (PostDTO)
    PostController->>PostController: Validate Input (@Valid)
    PostController->>PostService: createPost(userId, PostDTO)
    
    PostService->>UserRepository: findById(userId)
    UserRepository->>Database: SELECT ...
    Database-->>UserRepository: Optional<User>

    alt User Exists
        PostService->>PostService: Create Post Entity using User
        PostService->>PostRepository: save(Post)
        PostRepository->>Database: INSERT INTO posts ...
        Database-->>PostRepository: Saved Post
        PostRepository-->>PostService: Saved Post
        PostService-->>PostController: PostDTO
        PostController-->>Client: 201 Created (PostDTO)
    else User Not Found
        PostService-->>PostController: Throw ResourceNotFoundException
        PostController-->>Client: 404 Not Found (ErrorResponse)
    end
```

### 2.2 Get Posts by User (Paginated)
**Endpoint:** `GET /users/{userId}/posts?page=0&size=10`

- Returns a `Page<PostDTO>`.

```mermaid
sequenceDiagram
    participant Client
    participant PostController
    participant PostService
    participant UserRepository
    participant PostRepository
    participant Database

    Client->>PostController: GET /users/{userId}/posts?page=0&size=10
    PostController->>PostService: getPostsByUserId(userId, Pageable)
    
    PostService->>UserRepository: existsById(userId)
    
    alt User Exists
        PostService->>PostRepository: findByUserId(userId, Pageable)
        PostRepository->>Database: SELECT ... LIMIT ? OFFSET ?
        Database-->>PostRepository: Page<Post>
        PostRepository-->>PostService: Page<Post>
        PostService->>PostService: Map to Page<PostDTO>
        PostService-->>PostController: Page<PostDTO>
        PostController-->>Client: 200 OK (Page<PostDTO>)
    else User Not Found
        PostService-->>PostController: Throw ResourceNotFoundException
        PostController-->>Client: 404 Not Found (ErrorResponse)
    end
```
