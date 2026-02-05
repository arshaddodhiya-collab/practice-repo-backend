## 🔹 What is Spring Boot Lifecycle?

**Spring Boot lifecycle = the sequence of steps Spring Boot follows to:**

1. Start the application
2. Create & wire beans
3. Start the web server
4. Serve requests
5. Shut down cleanly

Think of it like **“birth → growth → work → death”** of the application.

---

## 🧠 High-Level Lifecycle Flow

![Image](https://miro.medium.com/1%2A5MShKzJV3ClbCRHHWxIWIw.png)

![Image](https://miro.medium.com/0%2AE61NwTVlG-elVoD_)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2A5r_dyCL2tcKXJ5ThSHlCyQ.png)

```
main()
 ↓
SpringApplication.run()
 ↓
ApplicationContext created
 ↓
Beans created & injected
 ↓
Server (Tomcat) starts
 ↓
Application ready
 ↓
Handles requests
 ↓
Shutdown
```

---

## 🟢 STEP 1: Application Starts (`main` method)

```java
@SpringBootApplication
public class PracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeApplication.class, args);
    }
}
```

👉 This single line starts **everything**.

What happens internally:

* Bootstraps Spring
* Sets up environment
* Starts auto-configuration

---

## 🟡 STEP 2: Environment & Configuration Loaded

Spring Boot loads:

* `application.properties` / `application.yml`
* Active profiles (`dev`, `prod`)
* Command-line arguments
* OS environment variables

Order of priority (important):

```
Command line args
↓
application.yml
↓
default values
```

---

## 🟠 STEP 3: ApplicationContext Created

**ApplicationContext = Spring container**

It is responsible for:

* Creating beans
* Injecting dependencies
* Managing lifecycle

At this point:
❌ No beans created yet
✅ Container is ready

---

## 🔵 STEP 4: Component Scanning

Spring scans packages starting from:

```java
@SpringBootApplication
```

It finds:

* `@Component`
* `@Service`
* `@Repository`
* `@Controller`
* `@RestController`

👉 These become **bean definitions**

---

## 🟣 STEP 5: Bean Creation & Dependency Injection

For every bean:

```
1. Constructor called
2. Dependencies injected
3. @PostConstruct executed
```

### Example

```java
@Component
public class Engine {

    public Engine() {
        System.out.println("1️⃣ Constructor");
    }

    @PostConstruct
    public void init() {
        System.out.println("2️⃣ Bean initialized");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("3️⃣ Bean destroyed");
    }
}
```

---

## 🧩 STEP 6: Auto-Configuration Magic

Spring Boot:

* Detects classpath
* Configures beans automatically

Examples:

* If MySQL dependency exists → DataSource created
* If Spring Web exists → Tomcat started
* If JPA exists → EntityManager configured

👉 This is **auto-configuration**

---

## 🟤 STEP 7: Embedded Server Starts

If it’s a web app:

* Embedded **Tomcat / Jetty / Netty** starts
* Binds to port (default `8080`)

You’ll see:

```
Tomcat started on port 8080
```

Now the app is **LIVE** 🚀

---

## 🟢 STEP 8: Application Ready

Spring fires events:

* `ApplicationStartedEvent`
* `ApplicationReadyEvent`

You can hook into this:

```java
@Component
public class StartupListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("✅ App is ready to serve requests");
    }
}
```

---

## 🔴 STEP 9: Application Running (Steady State)

At this stage:

* APIs accept requests
* DB connections are pooled
* Security filters active
* Schedulers running

This is **90% of app lifetime**.

---

## ⚫ STEP 10: Graceful Shutdown

Triggered by:

* CTRL + C
* Server stop
* Container shutdown

Spring:

1. Stops accepting new requests
2. Calls `@PreDestroy`
3. Closes DB connections
4. Stops server

```java
@PreDestroy
public void cleanup() {
    System.out.println("🛑 Cleaning resources");
}
```

---

## 🧠 Lifecycle Summary (One Screen)

```
main()
 ↓
Load config
 ↓
Create context
 ↓
Scan components
 ↓
Create beans
 ↓
Inject dependencies
 ↓
@PostConstruct
 ↓
Start server
 ↓
App ready
 ↓
Handle requests
 ↓
@PreDestroy
 ↓
Shutdown
```

---

## 🧾 Interview-Ready Answer

> “Spring Boot lifecycle starts with application bootstrap, loads configuration, creates application context, initializes beans with dependency injection, starts the embedded server, serves requests, and finally shuts down gracefully by destroying beans.”

---

