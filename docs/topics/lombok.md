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
| `@ToString`           | toString()        |
| `@EqualsAndHashCode`  | equals + hashCode |
| `@Data`               | All of the above  |
| `@Builder`            | Builder pattern   |

---

## 🎯 One-line Summary

> **Lombok removes boilerplate Java code so you can focus on business logic instead of getters and setters.**
