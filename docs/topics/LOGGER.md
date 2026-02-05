**Logger** is one of those things that *separates a “working app” from a “production-grade app”*. In HMS-level systems, logging is **non-negotiable**.

Let’s go step by step, from intuition → code → real production usage.

---

## 🧠 What is a Logger in Spring Boot?

> A **logger** is a mechanism to **record what your application is doing at runtime**.

Logs answer questions like:

* What request came in? 
* What decision did the system make?
* Why did something fail?
* What data was involved (safely)?

Think of logger as the **black box of your backend** ✈️

---

## 🏥 Why logging is CRITICAL in HMS

In a Hospital Management System:

* You **cannot reproduce production issues easily**
* Bugs happen with **real patients & money**
* Debugging via `System.out.println()` is impossible

👉 Logs become your **only truth**

---

## ❌ Why NOT `System.out.println()`?

| System.out.println | Logger           |
| ------------------ | ---------------- |
| No levels          | Log levels       |
| No file rotation   | File + rotation  |
| No timestamps      | Timestamps       |
| Not thread-safe    | Thread-safe      |
| No prod support    | Production-ready |

**Never used in production.**

---

## 1️⃣ Logger in Spring Boot (what’s inside)

Spring Boot uses:

* **SLF4J** (API)
* **Logback** (default implementation)

You write logs via SLF4J → Logback writes them to console/files.

---

## 2️⃣ Basic logger example

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PatientService {

    private static final Logger log =
        LoggerFactory.getLogger(PatientService.class);

    public void admitPatient(Long patientId) {
        log.info("Admitting patient with id={}", patientId);
    }
}
```

👉 `{}` is **placeholder** (lazy & safe)

---

## 3️⃣ Log levels (VERY important)

| Level   | When to use          |
| ------- | -------------------- |
| `TRACE` | Extremely detailed   |
| `DEBUG` | Developer debugging  |
| `INFO`  | Business flow        |
| `WARN`  | Something suspicious |
| `ERROR` | Something failed     |

### Example:

```java
log.debug("Request payload: {}", request);
log.info("Patient {} admitted", patientId);
log.warn("Bed {} is almost full", bedId);
log.error("Billing failed for patient {}", patientId, ex);
```

---

## 4️⃣ How logging works internally

```
Your Code
   ↓
SLF4J Logger
   ↓
Logback
   ↓
Console / File / ELK / Cloud
```

---

## 5️⃣ Logger configuration (production-style)

### application.yml

```yaml
logging:
  level:
    root: INFO
    com.hms.patient: DEBUG
  file:
    name: /var/log/hms/application.log
```

* Root logs → INFO
* Patient module → DEBUG

---

## 6️⃣ File rotation (VERY IMPORTANT)

Without rotation → disk full → app crash ❌

### logback-spring.xml

```xml
<configuration>
  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>/var/log/hms/app.log</file>

    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>/var/log/hms/app.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>

    <encoder>
      <pattern>%d %-5level [%thread] %logger - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="FILE"/>
  </root>
</configuration>
```

---

## 7️⃣ Logging best practices (REAL HMS RULES)

### ✅ Log:

* Request start/end
* IDs (patientId, billId)
* State transitions
* Exceptions

### ❌ Never log:

* Passwords
* OTPs
* Tokens
* Aadhaar / PAN
* Medical reports content

---

## 8️⃣ Logger + Actuator (power combo)

Remember Actuator?

You can **change log level at runtime**:

```
POST /actuator/loggers/com.hms.patient
{
  "configuredLevel": "DEBUG"
}
```

👉 No restart
👉 Debug live issue
👉 Turn back to INFO

---

## 9️⃣ Real HMS logging example

```java
@Transactional
public void generateBill(Long patientId) {

    log.info("Bill generation started for patientId={}", patientId);

    try {
        Bill bill = billingRepo.create(patientId);
        log.info("Bill {} created successfully", bill.getId());

    } catch (Exception ex) {
        log.error("Billing failed for patientId={}", patientId, ex);
        throw ex;
    }
}
```

This log can:

* Reconstruct entire incident
* Be audited
* Be monitored

---

## 🔥 Interview-ready definition

> **Logger in Spring Boot is a structured, level-based, thread-safe logging mechanism (via SLF4J & Logback) used to record application behavior, errors, and business flow, enabling debugging, monitoring, auditing, and incident investigation in production systems.**

---
