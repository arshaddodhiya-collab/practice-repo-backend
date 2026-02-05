## 🔹 What is Dependency Injection (DI)?

**Dependency Injection = Spring gives objects to a class instead of the class creating them**

> “Don’t call me, I’ll call you.” — Spring 😄

### Simple definition

* **Dependency** → an object a class needs
* **Injection** → providing that object automatically

---

## ❌ Without Dependency Injection (Bad Way)

```java
class Engine {
    void start() {
        System.out.println("Engine started");
    }
}

class Car {
    private Engine engine = new Engine(); // ❌ tight coupling

    void drive() {
        engine.start();
    }
}
```

### Problems

* Hard to test
* Tight coupling
* No flexibility

---

## ✅ With Dependency Injection (Spring Boot Way)

```java
@Component
class Engine {
    void start() {
        System.out.println("Engine started");
    }
}

@Component
class Car {

    private final Engine engine;

    // ✅ Constructor Injection
    public Car(Engine engine) {
        this.engine = engine;
    }

    void drive() {
        engine.start();
    }
}
```

### What Spring does internally

1. Creates `Engine`
2. Creates `Car`
3. Injects `Engine` into `Car`

👉 **This is Dependency Injection**

---

## 🧠 Types of Dependency Injection in Spring Boot

### 1️⃣ Constructor Injection (BEST PRACTICE ⭐)

```java
@RestController
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }
}
```

✔ Recommended
✔ Mandatory dependencies
✔ Easy unit testing

---

### 2️⃣ Field Injection (NOT recommended ❌)

```java
@Autowired
private CarService carService;
```

❌ Hidden dependencies
❌ Hard to test

---

### 3️⃣ Setter Injection (Optional use)

```java
@Autowired
public void setCarService(CarService carService) {
    this.carService = carService;
}
```

✔ Optional dependency
❌ Not ideal for required objects

---

## 🔹 Annotations Used in DI

| Annotation        | Purpose              |
| ----------------- | -------------------- |
| `@Component`      | Generic bean         |
| `@Service`        | Business logic       |
| `@Repository`     | DB layer             |
| `@RestController` | API layer            |
| `@Autowired`      | Inject dependency    |
| `@Configuration`  | Config class         |
| `@Bean`           | Manual bean creation |

---

## 🔁 DI in Real Spring Boot Flow

```
Controller
   ↓ (injects)
Service
   ↓ (injects)
Repository
   ↓
Database
```

### Example

```java
@RestController
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }
}
```

```java
@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
```

```java
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
```

---

## 🧠 How Spring Knows What to Inject?

* Scans classes with `@Component`
* Registers them as beans
* Matches **type**
* Injects automatically

If multiple beans exist → use `@Qualifier`

```java
@Autowired
@Qualifier("petrolEngine")
private Engine engine;
```

---

## ❓ DI vs IoC (Interview Favorite)

| IoC                     | DI                     |
| ----------------------- | ---------------------- |
| Concept                 | Implementation         |
| Spring controls objects | Spring injects objects |
| Big idea                | Practical usage        |

---

## 🧾 Interview-Ready Answer

> “Dependency Injection in Spring Boot is a design pattern where Spring automatically provides required dependencies to a class, improving loose coupling, testability, and maintainability.”

---

## 🎯 Key Rules to Remember

✔ Prefer **constructor injection**
✔ Avoid field injection
✔ One responsibility per layer
✔ Let Spring manage objects

---


