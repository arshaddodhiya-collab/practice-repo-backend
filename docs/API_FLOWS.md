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

---

## 3. Comment Management

### 3.1 Add Comment
**Endpoint:** `POST /comments`

**Flow:**
1.  Client sends `POST /comments` with `CommentDTO` (text, userId, postId).
2.  `CommentController` calls service.
3.  `CommentService` verifies User and Post exist.
4.  `CommentService` creates and saves `Comment` entity.
5.  Returns created `CommentDTO`.

```mermaid
sequenceDiagram
    participant Client
    participant CommentController
    participant CommentService
    participant UserRepository
    participant PostRepository
    participant CommentRepository
    participant Database

    Client->>CommentController: POST /comments (CommentDTO)
    CommentController->>CommentService: addComment(CommentDTO)
    
    CommentService->>UserRepository: findById(userId)
    alt User Not Found
        CommentService-->>CommentController: Throw ResourceNotFoundException
        CommentController-->>Client: 404 Not Found
    end
    
    CommentService->>PostRepository: findById(postId)
    alt Post Not Found
        CommentService-->>CommentController: Throw ResourceNotFoundException
        CommentController-->>Client: 404 Not Found
    end

    CommentService->>CommentService: Create Comment Entity
    CommentService->>CommentRepository: save(Comment)
    CommentRepository->>Database: INSERT INTO comments ...
    Database-->>CommentRepository: Saved Entity
    CommentRepository-->>CommentService: Saved Entity
    CommentService-->>CommentController: CommentDTO
    CommentController-->>Client: 201 Created (CommentDTO)
```

### 3.2 Get Comments by Post (HQL)
**Endpoint:** `GET /comments/post/{postId}`

- Uses **HQL** to fetch comments and associated users in a single query (`JOIN FETCH`).

```mermaid
sequenceDiagram
    participant Client
    participant CommentController
    participant CommentService
    participant CommentRepository
    participant Database

    Client->>CommentController: GET /comments/post/{postId}
    CommentController->>CommentService: getCommentsByPostId(postId)
    CommentService->>CommentRepository: findCommentsWithUserByPostId(postId)
    CommentRepository->>Database: SELECT c, u FROM Comment c JOIN FETCH c.user ...
    Database-->>CommentRepository: List<Comment> (with Users initialized)
    CommentRepository-->>CommentService: List<Comment>
    CommentService->>CommentService: Map to List<CommentDTO>
    CommentService-->>CommentController: List<CommentDTO>
    CommentController-->>Client: 200 OK
```

---

## 4. Like Management

### 4.1 Like a Post
**Endpoint:** `POST /likes`

**Flow:**
1. Check if user already liked the post.
2. Verify User and Post exist.
3. Save `PostLike` entity.

```mermaid
sequenceDiagram
    participant Client
    participant PostLikeController
    participant PostLikeService
    participant PostLikeRepository
    participant Database

    Client->>PostLikeController: POST /likes (PostLikeDTO)
    PostLikeController->>PostLikeService: likePost(PostLikeDTO)
    
    PostLikeService->>PostLikeRepository: existsByUserIdAndPostId(...)
    alt Already Liked
        PostLikeService-->>PostLikeController: Throw Exception
        PostLikeController-->>Client: 400 Bad Request
    end

    PostLikeService->>PostLikeService: Verify User & Post Exist
    PostLikeService->>PostLikeRepository: save(PostLike)
    PostLikeRepository->>Database: INSERT INTO post_likes ...
    Database-->>PostLikeRepository: Saved
    PostLikeRepository-->>PostLikeService: Saved
    PostLikeService-->>PostLikeController: void
    PostLikeController-->>Client: 201 Created
```

### 4.2 Count Likes (Native Query)
**Endpoint:** `GET /likes/post/{postId}/count`

- Uses **Native SQL** for performance.

```mermaid
sequenceDiagram
    participant Client
    participant PostLikeController
    participant PostLikeService
    participant PostLikeRepository
    participant Database

    Client->>PostLikeController: GET /likes/post/{postId}/count
    PostLikeController->>PostLikeService: countLikes(postId)
    PostLikeService->>PostLikeRepository: countLikesByPostId(postId)
    PostLikeRepository->>Database: SELECT COUNT(*) FROM post_likes ...
    Database-->>PostLikeRepository: count (Long)
    PostLikeRepository-->>PostLikeService: count
    PostLikeService-->>PostLikeController: count
    PostLikeController-->>Client: 200 OK (Count)
```

---

## 5. Reports (Native Queries)

### 5.1 Top Active Users
**Endpoint:** `GET /reports/active-users`

- Uses complex **Native SQL** with `JOIN` and `GROUP BY`.
- Maps result to `UserActivityReportDTO` interface.

```mermaid
sequenceDiagram
    participant Client
    participant ReportController
    participant PostLikeService
    participant PostLikeRepository
    participant Database

    Client->>ReportController: GET /reports/active-users
    ReportController->>PostLikeService: getTopActiveUsers()
    PostLikeService->>PostLikeRepository: findTopActiveUsers()
    PostLikeRepository->>Database: SELECT u.id, u.name, (...) FROM users ...
    Database-->>PostLikeRepository: ResultSet
    PostLikeRepository->>PostLikeRepository: Map to UserActivityReportDTO (Projection)
    PostLikeRepository-->>PostLikeService: List<UserActivityReportDTO>
    PostLikeService-->>ReportController: List<UserActivityReportDTO>
    ReportController-->>Client: 200 OK (JSON List)
```

---

## 6. Category Management

### 6.1 Create Category
**Endpoint:** `POST /categories`

**Flow:**
1. Client sends `POST /categories` with name and description.
2. `CategoryController` calls `CategoryService`.
3. `CategoryService` saves the entity.
4. Returns created `CategoryDTO`.

```mermaid
sequenceDiagram
    participant Client
    participant CategoryController
    participant CategoryService
    participant CategoryRepository
    participant Database

    Client->>CategoryController: POST /categories (CategoryDTO)
    CategoryController->>CategoryService: createCategory(CategoryDTO)
    CategoryService->>CategoryRepository: save(Category)
    CategoryRepository->>Database: INSERT INTO categories ...
    Database-->>CategoryRepository: Saved Category
    CategoryRepository-->>CategoryService: Saved Category
    CategoryService->>CategoryService: Map to DTO
    CategoryService-->>CategoryController: CategoryDTO
    CategoryController-->>Client: 201 Created (CategoryDTO)
```

### 6.2 Get All Categories
**Endpoint:** `GET /categories`

- Returns a list of all categories.

### 6.3 Get Posts by Category
**Endpoint:** `GET /categories/{id}/posts`

- Returns all posts belonging to a specific category.

```mermaid
sequenceDiagram
    participant Client
    participant CategoryController
    participant CategoryService
    participant CategoryRepository
    participant PostRepository
    participant Database

    Client->>CategoryController: GET /categories/{id}/posts
    CategoryController->>CategoryService: getPostsByCategory(id)
    CategoryService->>CategoryRepository: existsById(id)
    
    alt Category Exists
        CategoryService->>PostRepository: findByCategoryId(id)
        PostRepository->>Database: SELECT * FROM posts WHERE category_id = ?
        Database-->>PostRepository: List<Post>
        PostRepository-->>CategoryService: List<Post>
        CategoryService->>CategoryService: Map to PostDTOs
        CategoryService-->>CategoryController: List<PostDTO>
        CategoryController-->>Client: 200 OK (List<PostDTO>)
    else Category Not Found
        CategoryService-->>CategoryController: Throw ResourceNotFoundException
        CategoryController-->>Client: 404 Not Found
    end
```
