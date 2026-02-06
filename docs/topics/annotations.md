## 🧠 Big Picture First (Don’t Skip This)

**Annotations in Spring Boot answer four questions:**

1. **WHAT is this class / method / field?**
2. **WHEN should Spring use it?**
3. **WHERE should Spring manage it?**
4. **HOW should Spring wire things together?**

Spring Boot scans your code, reads annotations, and builds the application **at runtime**.

---

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/1%2AlRYT83RhbWw-4_Uu9cTdXg.png)

![Image](https://miro.medium.com/0%2AEu5gNw91iJNpSqMr)

![Image](https://www.springboottutorial.com/images/spring-features.png)

---

# 🧩 ANNOTATION CATEGORIES (Mental Map)

You’ll understand better if you group them:

| Category             | Purpose                      |
| -------------------- | ---------------------------- |
| Core Boot            | Start & configure app        |
| Stereotype           | Tell Spring “this is a bean” |
| Dependency Injection | Wire objects                 |
| Web / REST           | Handle HTTP                  |
| JPA / Database       | Persistence                  |
| Configuration        | App settings                 |
| Validation           | Input checks                 |
| Security             | Auth & access                |
| Utility              | Misc helpers                 |

---

# 1️⃣ CORE SPRING BOOT ANNOTATIONS

---

## 🔹 `@SpringBootApplication`

### WHAT

Main entry point of your app.

### WHEN

Used **once**, on the main class.

### WHERE

Top-level package.

### HOW

It combines **three annotations**:

* `@Configuration`
* `@EnableAutoConfiguration`
* `@ComponentScan`

```java
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

🧠 **Rule**: Put this class at the **root package**, or Spring won’t scan properly.

---

## 🔹 `@EnableAutoConfiguration`

### WHAT

Spring decides configs automatically.

### WHEN

At startup.

### HOW

Checks:

* Dependencies
* Classpath
* application.properties

📌 Example:

* If MySQL exists → auto config DataSource
* If Spring MVC exists → auto config DispatcherServlet

---

# 2️⃣ STEREOTYPE ANNOTATIONS (BEANS)

These tell Spring:
👉 “Create and manage this object”

---

## 🔹 `@Component`

### WHAT

Generic Spring-managed bean.

### WHEN

During component scanning.

### WHERE

Any class.

```java
@Component
public class EmailService { }
```

---

## 🔹 `@Service`

### WHAT

Business logic layer.

### WHY

Same as `@Component`, but **semantic clarity**.

```java
@Service
public class UserService { }
```

🧠 Best for:

* Business rules
* Transactions
* Processing logic

---

## 🔹 `@Repository`

### WHAT

Data access layer.

### SPECIAL POWER

* Translates DB exceptions into Spring exceptions

```java
@Repository
public class UserRepository { }
```

🧠 Used automatically by Spring Data JPA interfaces.

---

## 🔹 `@Controller` vs `@RestController`

### `@Controller`

Returns **views (HTML)**

```java
@Controller
public class PageController {
    @GetMapping("/home")
    public String home() {
        return "index";
    }
}
```

---

### `@RestController`

### WHAT

REST APIs → returns **JSON**

### HOW

`@Controller + @ResponseBody`

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public List<User> users() {
        return service.getAll();
    }
}
```

🧠 **99% of backend devs use this**

---

# 3️⃣ DEPENDENCY INJECTION ANNOTATIONS

---

## 🔹 `@Autowired`

### WHAT

Injects dependency automatically.

### WHERE

* Constructor (BEST)
* Field (NOT recommended)
* Setter

```java
@Service
public class OrderService {

    private final UserService userService;

    @Autowired
    public OrderService(UserService userService) {
        this.userService = userService;
    }
}
```

🧠 **Constructor injection = best practice**

---

## 🔹 `@Qualifier`

### WHAT

Choose which bean to inject.

```java
@Autowired
@Qualifier("paypalPayment")
private PaymentService paymentService;
```

---

## 🔹 `@Primary`

### WHAT

Default bean if multiple exist.

```java
@Primary
@Component
public class StripePayment implements PaymentService {}
```

---

# 4️⃣ WEB / REST ANNOTATIONS

---

## 🔹 `@RequestMapping`

Base mapping.

```java
@RequestMapping("/api/users")
```

---

## 🔹 HTTP Method Mappings

| Annotation       | HTTP   |
| ---------------- | ------ |
| `@GetMapping`    | GET    |
| `@PostMapping`   | POST   |
| `@PutMapping`    | PUT    |
| `@DeleteMapping` | DELETE |
| `@PatchMapping`  | PATCH  |

```java
@GetMapping("/{id}")
public User get(@PathVariable Long id) { }
```

---

## 🔹 `@PathVariable`

```java
@GetMapping("/users/{id}")
public User get(@PathVariable Long id) { }
```

---

## 🔹 `@RequestParam`

```java
@GetMapping("/search")
public List<User> search(@RequestParam String name) { }
```

---

## 🔹 `@RequestBody`

```java
@PostMapping
public User save(@RequestBody User user) { }
```

---

# 5️⃣ JPA / DATABASE ANNOTATIONS

---

## 🔹 `@Entity`

### WHAT

Maps class → DB table.

```java
@Entity
public class User { }
```

---

## 🔹 `@Id`

Primary key.

```java
@Id
@GeneratedValue
private Long id;
```

---

## 🔹 `@Column`

```java
@Column(nullable = false, unique = true)
private String email;
```

---

## 🔹 Relationships

| Annotation    | Relation |
| ------------- | -------- |
| `@OneToOne`   | 1–1      |
| `@OneToMany`  | 1–N      |
| `@ManyToOne`  | N–1      |
| `@ManyToMany` | N–N      |

```java
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

---

# 6️⃣ CONFIGURATION ANNOTATIONS

---

## 🔹 `@Configuration`

Defines config class.

```java
@Configuration
public class AppConfig { }
```

---

## 🔹 `@Bean`

Manually create bean.

```java
@Bean
public ObjectMapper mapper() {
    return new ObjectMapper();
}
```

---

## 🔹 `@Value`

Inject property.

```java
@Value("${server.port}")
private int port;
```

---

## 🔹 `@ConfigurationProperties`

Bind whole config object.

```java
@ConfigurationProperties(prefix = "app")
public class AppConfigProps {
    private String name;
}
```

---

# 7️⃣ VALIDATION ANNOTATIONS

---

```java
@NotNull
@NotBlank
@Email
@Size(min = 8)
```

```java
public class UserDTO {
    @Email
    private String email;
}
```

Activate using:

```java
@PostMapping
public void save(@Valid @RequestBody UserDTO dto) { }
```

---

# 8️⃣ SECURITY ANNOTATIONS (IMPORTANT)

---

## 🔹 `@EnableWebSecurity`

Enable Spring Security.

---

## 🔹 `@PreAuthorize`

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public String admin() { }
```

---

## 🔹 `@Secured`

```java
@Secured("ROLE_ADMIN")
```

---

# 9️⃣ UTILITY / SPECIAL ANNOTATIONS

---

## 🔹 `@Transactional`

### WHAT

Wraps method in DB transaction.

```java
@Transactional
public void transfer() { }
```

🧠 Rollback on runtime exceptions.

---

## 🔹 `@Async`

```java
@Async
public void sendEmail() { }
```

---

## 🔹 `@Scheduled`

```java
@Scheduled(fixedRate = 5000)
public void job() { }
```

---

# 🧠 FINAL MENTAL MODEL (REMEMBER THIS)

> **Spring Boot = Annotation + Convention + Runtime Magic**

Annotations:

* Tell Spring **what to create**
* Tell Spring **how to wire**
* Tell Spring **when to execute**