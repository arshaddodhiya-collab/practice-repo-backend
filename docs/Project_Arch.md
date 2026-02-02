## 🌱 What is Spring Boot Architecture ?

Think of a **Spring Boot project like a hospital** 🏥

| Hospital        | Spring Boot                         |
| --------------- | ----------------------------------- |
| Patient comes   | Client (Browser / Mobile / Postman) |
| Reception       | Controller                          |
| Doctor          | Service                             |
| Medical Records | Repository                          |
| Database Room   | Database                            |

➡️ **Request comes in → processed step-by-step → response goes out**

---

## 🧱 Basic Spring Boot Layers (Architecture)

```
Client
  ↓
Controller (REST APIs)
  ↓
Service (Business Logic)
  ↓
Repository (Database Logic)
  ↓
Database (MySQL, PostgreSQL, etc.)
```

---

## 📁 Typical Spring Boot Project Structure

```
com.artem.hmis
│
├── controller
│   └── PatientController.java
│
├── service
│   ├── PatientService.java
│   └── impl
│       └── PatientServiceImpl.java
│
├── repository
│   └── PatientRepository.java
│
├── entity
│   └── Patient.java
│
├── dto
│   └── PatientDTO.java
│
├── exception
│   └── GlobalExceptionHandler.java
│
├── config
│   └── SecurityConfig.java
│
└── Application.java
```

Each folder has **one clear responsibility**.

---

## 🔹 Layer-by-Layer Explanation (Very Simple)

---

## 1️⃣ Controller Layer (Reception Desk)

👉 **What it does**

* Accepts HTTP requests
* Returns HTTP responses
* No business logic

📌 Example:

```java
@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.save(patient);
    }
}
```

🧠 Think of it as:

> “Okay patient arrived, let me send him to the right doctor”

---

## 2️⃣ Service Layer (Doctor 🧑‍⚕️)

👉 **What it does**

* Contains business rules
* Handles validations
* Calls repository

📌 Example:

```java
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public Patient save(Patient patient) {
        // business logic
        if(patient.getAge() < 0) {
            throw new RuntimeException("Invalid age");
        }
        return patientRepository.save(patient);
    }
}
```

🧠 Think:

> “Doctor decides what treatment to give”

---

## 3️⃣ Repository Layer (Medical Records)

👉 **What it does**

* Talks to database
* No business logic
* Uses JPA / Hibernate

📌 Example:

```java
@Repository
public interface PatientRepository 
        extends JpaRepository<Patient, Long> {
}
```

🧠 Think:

> “Fetch and store patient records”

---

## 4️⃣ Entity Layer (Database Table Mapping)

👉 **What it does**

* Represents DB table
* Uses JPA annotations

📌 Example:

```java
@Entity
public class Patient {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int age;
}
```

🧠 Think:

> “One Java object = One DB row”

---

## 5️⃣ DTO Layer (Safe Data Transfer)

👉 **Why needed**

* Avoid exposing full entity
* Control response structure

📌 Example:

```java
public class PatientDTO {
    private String name;
    private int age;
}
```

🧠 Production rule:

> **Never expose entities directly in large apps**

---

## 6️⃣ Exception Handling (Error Manager)

👉 Central place to handle errors

📌 Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
```

🧠 Think:

> “If anything goes wrong, handle it cleanly”

---

## 🔄 Full Request Flow (Step by Step)

```
1. Client sends POST /patients
2. Controller receives request
3. Controller calls Service
4. Service applies business rules
5. Service calls Repository
6. Repository talks to DB
7. Data saved
8. Response returned to client
```

---

## 🏭 How This Looks in Production (Real Companies)

In **large HMIS / enterprise apps**, they also add:

✅ **Security Layer** (JWT, OAuth2)
✅ **Logging** (ELK / Logback)
✅ **Caching** (Redis)
✅ **DTO + Mapper** (MapStruct)
✅ **Config Server**
✅ **Actuator** (health checks)

Architecture becomes:

```
Controller
   ↓
Service
   ↓
Domain / Business Layer
   ↓
Repository
   ↓
DB
```

---

## 🧠 Key Rules Used in Real Projects

✔ Controller = thin
✔ Service = fat (business logic)
✔ Repository = only DB
✔ Entity ≠ DTO
✔ Exception handling centralized
✔ Each layer independent

---