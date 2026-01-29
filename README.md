   git clone <repository-url>
   cd practice-backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Usage

### Run the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/users` | Create a new user |
| `GET` | `/users` | Get all users |
| `GET` | `/users/{id}` | Get a user by ID |
| `DELETE` | `/users/{id}` | Delete a user by ID |

### Example Requests

**Create User**
```bash
curl -X POST http://localhost:8080/users \
     -H "Content-Type: application/json" \
     -d '{"name": "John Doe", "email": "[EMAIL_ADDRESS]"}'
```

**Get All Users**
```bash
curl http://localhost:8080/users
```

**Get User by ID**
```bash
curl http://localhost:8080/users/1
```

**Delete User**
```bash
curl -X DELETE http://localhost:8080/users/1
```

## Database

This application uses an H2 in-memory database. The database is automatically created when the application starts and is cleared when the application stops.

You can access the H2 console at `http://localhost:8080/h2-console`.

## Testing

Run the tests using Maven:

```bash
mvn test
