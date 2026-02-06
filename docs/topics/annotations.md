## 🤔 What problem does Lombok solve?

In **normal Java**, you write **a LOT of boring code**.

Example: a simple `User` class 👇

```java
public class User {

    private Long id;
    private String name;
    private String email;

    public User() {}

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

👉 **80% of this code is noise**
👉 Hard to read
👉 Easy to forget a getter/setter
👉 Makes classes unnecessarily long

---

## ✨ What is Lombok?

**Lombok is a Java library that writes this boring code for you at compile time.**

You write **less code**, but Java still behaves **exactly the same**.

Think of Lombok as:

> 🧠 “Hey compiler, please generate getters, setters, constructors, etc. for me.”

---

## 🔧 How Lombok works (simple idea)

* You add **annotations** (like `@Getter`, `@Setter`)
* Lombok **generates code during compilation**
* **No extra code at runtime**
* Your `.class` file has everything

⚠️ Lombok does **NOT** exist in production JVM
It’s a **developer convenience tool**

---

## 📦 Adding Lombok in Spring Boot

If you used **Spring Initializr**, just check ✅ Lombok.

### Maven dependency

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

⚠️ Also install **Lombok plugin** in your IDE
(IntelliJ / Eclipse / VS Code)

---

## 🧩 Most Used Lombok Annotations (You’ll use these daily)

---

### 1️⃣ `@Getter` and `@Setter`

Instead of writing getters/setters manually:

```java
@Getter
@Setter
public class User {
    private Long id;
    private String name;
}
```

➡️ Lombok generates:

* `getId()`, `setId()`
* `getName()`, `setName()`

You can also apply it **per field**:

```java
@Getter
private String name;
```

---

### 2️⃣ `@NoArgsConstructor` & `@AllArgsConstructor`

Very common in **Spring + JPA**

```java
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
}
```

Equivalent to:

```java
public User() {}
public User(Long id, String name) { ... }
```

---

### 3️⃣ `@ToString`

Auto-generates `toString()` method

```java
@ToString
public class User {
    private Long id;
    private String name;
}
```

Now logging becomes easy:

```java
System.out.println(user);
```

---

### 4️⃣ ⭐ `@Data` (MOST USED)

This is a **combo annotation** 🔥

```java
@Data
public class User {
    private Long id;
    private String name;
}
```

It generates:
✔ getters
✔ setters
✔ `toString()`
✔ `equals()`
✔ `hashCode()`
✔ required constructor

👉 **90% of DTOs use `@Data`**

---

### 5️⃣ `@Builder` (Very powerful)

Used for **clean object creation**

```java
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}
```

Usage:

```java
User user = User.builder()
        .id(1L)
        .name("Arashad")
        .email("a@test.com")
        .build();
```

✔ readable
✔ avoids constructor overload mess
✔ common in APIs

---

## 🧪 Lombok in a Spring Boot Example (Realistic)

### Entity / DTO example

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Patient {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int age;
}
```

👉 Without Lombok this file would be **2x longer**

---

## ⚠️ Lombok with JPA – Important Tips

❌ **Avoid `@Data` on JPA entities blindly**

Why?

* `equals()` & `hashCode()` can break Hibernate
* `toString()` may cause lazy-loading issues

### ✅ Better JPA pattern

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Patient {
    ...
}
```

Use `@Data` freely for:
✔ DTOs
✔ Request/Response models
✔ Utility classes

---

## 🧠 Is Lombok safe?

✔ Used by millions
✔ Used in Spring ecosystem
✔ No runtime overhead
✔ Compile-time only

❌ Downside:

* Hidden code (new devs may get confused)
* IDE plugin required

---

## 🧾 Quick Cheat Sheet

| Annotation            | Purpose           |
| --------------------- | ----------------- |
| `@Getter`             | Generates getters |
| `@Setter`             | Generates setters |
| `@NoArgsConstructor`  | Empty constructor |
| `@AllArgsConstructor` | Full constructor  |
| `@ToString`Love this question. This is exactly the *right* time to master annotations — once you get these, Spring Boot starts feeling **logical instead of magical** 🧠✨

I’ll give you a **future-proof, readable, reference-style guide** that you can come back to anytime.

---

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

---

If you want, next we can:

* 🔍 Map **annotations to internal Spring flow**
* 🔐 Deep-dive **security annotations**
* 🧪 Annotation behavior **at runtime**
* 📦 Real-world **project-wise annotation usage**

Just tell me how deep you want to go 😄
           | toString()        |
| `@EqualsAndHashCode`  | equals + hashCode |
| `@Data`               | All of the above  |
| `@Builder`            | Builder pattern   |

---

## 🎯 One-line Summary

> **Lombok removes boilerplate Java code so you can focus on business logic instead of getters and setters.**

