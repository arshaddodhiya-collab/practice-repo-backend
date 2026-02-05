# 🧱 ENTITY RULES (Deep + Simple)

---

## 1️⃣ No-Arg Constructor Requirement

### ❓ What is it?

Every JPA entity **must have a no-argument constructor**.

```java
@Entity
public class User {

    public User() {
    }
}
```

---

### 🤔 Why is it required?

Hibernate:

* Creates objects using **reflection**
* Does **not know your custom constructors**
* Needs a **default constructor** to instantiate entity

---

### ❌ What breaks without it?

```java
@Entity
public class User {

    private String name;

    public User(String name) {
        this.name = name;
    }
}
```

🚨 Runtime error:

```
No default constructor for entity
```

---

### ✅ Best Practice

```java
protected User() {
}
```

✔ Prevents accidental usage
✔ Still works for Hibernate

---

## 2️⃣ Why Entities Should NOT Be `final`

### ❓ Why would someone make it final?

To prevent inheritance or modification.

---

### ❌ Why Hibernate HATES `final`

Hibernate uses **proxies** for:

* Lazy loading
* Change tracking

Proxies = subclasses created at runtime.

```java
final class User ❌
```

Hibernate cannot extend it → BOOM 💥

---

### ❌ Example

```java
@Entity
public final class User {
}
```

🚨 Error:

```
Cannot create proxy for final class
```

---

### ✅ Rule

✔ Entity class → **NOT final**
✔ Methods → **NOT final**

---

## 3️⃣ equals() & hashCode() Pitfalls (VERY IMPORTANT)

### ❓ Why needed?

* Collections (`Set`, `Map`)
* Hibernate identity management
* Caching

---

### ❌ WRONG WAY (Using ID only)

```java
@Override
public boolean equals(Object o) {
    User u = (User) o;
    return id.equals(u.id);
}
```

🚨 Problem:

* ID is `null` before persistence
* Two new entities look “equal” incorrectly

---

### ❌ WRONG WAY (Using all fields)

```java
return name.equals(u.name) && email.equals(u.email);
```

🚨 Problems:

* Lazy fields trigger DB calls
* Performance issues
* Infinite loops in relations

---

### ✅ CORRECT WAY (Business Key)

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return email.equals(user.email);
}

@Override
public int hashCode() {
    return email.hashCode();
}
```

✔ Use **immutable, unique field**
✔ Example: email, username, UUID

---

### 🧠 Golden Rule

> Never use database-generated ID in equals/hashCode

---

## 4️⃣ Serializable — When & Why

### ❓ What is Serializable?

Allows object to be:

* Sent over network
* Stored in session
* Cached
* Converted to byte stream

---

### ❓ Do entities HAVE to implement Serializable?

❌ Not mandatory
✅ Recommended in **enterprise apps**

---

### When It’s Needed

✔ Distributed systems
✔ Caching (Redis, Ehcache)
✔ Session replication
✔ Messaging systems

---

### ✅ Example

```java
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
}
```

---

### 🧠 Simple Rule

> If entity may cross JVM boundaries → implement Serializable

---

# 🚀 ADVANCED MAPPING

---

## 5️⃣ Enum Mapping (`@Enumerated`)

### ❓ Problem

Java Enum ≠ Database column

---

### ❌ Default (ORDINAL) — DANGEROUS

```java
@Enumerated
private Status status;
```

Enum:

```java
ACTIVE, INACTIVE
```

DB stores:

```
0, 1
```

🚨 If enum order changes → data corruption

---

### ✅ SAFE WAY (STRING)

```java
@Enumerated(EnumType.STRING)
private Status status;
```

DB stores:

```
ACTIVE
```

---

### 🧠 Best Practice

✔ Always use `EnumType.STRING`

---

## 6️⃣ Date & Time Mapping (Java 8+)

### ❌ Old Way (Avoid)

```java
java.util.Date
```

Problems:

* Mutable
* Timezone confusion

---

### ✅ Modern Way

```java
private LocalDate dateOfBirth;
private LocalDateTime createdAt;
```

---

### How It Maps

| Java          | DB        |
| ------------- | --------- |
| LocalDate     | DATE      |
| LocalDateTime | TIMESTAMP |

---

### Example

```java
@Column(nullable = false)
private LocalDateTime createdAt;
```

Hibernate handles conversion automatically.

---

## 7️⃣ Embedded Objects (`@Embeddable`, `@Embedded`)

### ❓ Why?

Avoid repeating same fields in multiple entities.

---

### ❌ Bad Design

```java
private String street;
private String city;
private String zip;
```

Repeated everywhere 😵

---

### ✅ Correct Way

#### Step 1: Create Embeddable

```java
@Embeddable
public class Address {

    private String street;
    private String city;
    private String zip;
}
```

---

#### Step 2: Embed in Entity

```java
@Embedded
private Address address;
```

---

### 🧠 DB Table

```
street | city | zip
```

✔ No extra table
✔ Clean design

---

### Use Cases

* Address
* Money
* Audit info (createdBy, createdAt)

---

## 8️⃣ Transient Fields (`@Transient`)

### ❓ What is it?

Field **NOT stored in DB**

---

### Example

```java
@Transient
private Integer age;
```

---

### When to Use

✔ Calculated fields
✔ Temporary data
✔ API-only fields

---

### Example (Computed Value)

```java
@Transient
public int getAge() {
    return Period.between(dob, LocalDate.now()).getYears();
}
```

---

### ❌ Common Mistake

Forgetting `@Transient` → Hibernate tries to create column → error

---

## 🎯 FINAL ENTITY DESIGN CHECKLIST

✔ No-arg constructor
✔ Class NOT final
✔ equals/hashCode uses business key
✔ Serializable when needed
✔ EnumType.STRING
✔ Java 8 date/time
✔ Embedded reusable objects
✔ @Transient for non-persistent data

---

## 🔥 Interview One-Liners

**Why no-arg constructor?**
👉 Hibernate uses reflection.

**Why entity not final?**
👉 Hibernate creates proxy subclasses.

**Why not use ID in equals?**
👉 ID is null before persistence.

**Why EnumType.STRING?**
👉 Prevents data corruption.

