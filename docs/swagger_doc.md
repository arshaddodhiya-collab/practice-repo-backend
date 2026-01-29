# User REST API & Swagger Walkthrough

I have successfully implemented a full-stack [User](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/entity/User.java#9-52) REST API and integrated Swagger/OpenAPI for documentation.

## Changes Verified

### 1. User API
- **Endpoints Implemented:**
  - `POST /users` - Create a user
  - `GET /users` - List all users
  - `GET /users/{id}` - Get user by ID
  - `DELETE /users/{id}` - Delete user
- **Database:** Connected to MySQL (`practice_db`).
- **Entity:** [User](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/entity/User.java#9-52) (id, name, email).

### 2. Swagger Support
- **Swagger UI:** Accessible at `http://localhost:8080/swagger-ui/index.html`
- **Dependency:** Added `springdoc-openapi-starter-webmvc-ui`.

## Verification Results

### Swagger UI
Verified that the Swagger UI loads successfully:
```bash
curl -I http://localhost:8080/swagger-ui/index.html
HTTP/1.1 200 
...
```

### User API Test
Verified the API is responsive:
```bash
curl http://localhost:8080/users
[]
```
*(Currently returns an empty list, confirming the database is connected but empty)*

## How to Test
1. **Open Swagger UI:** Go to [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) in your browser.
2. **Use API:** You can try the "Try it out" button on the `/users` `POST` endpoint to create a user.
