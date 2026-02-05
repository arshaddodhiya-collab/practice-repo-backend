# ✅ Day 6 – JPA & Hibernate (Detailed + Simple)

## 1️⃣ ORM Concepts (Object Relational Mapping)

### ❓ What problem does ORM solve?

In Java:

* You work with **objects**
* Database works with **tables & rows**

👉 ORM is the **bridge** between Java objects and database tables.

### ❌ Without ORM (Old Way)

```java
ResultSet rs = statement.executeQuery("SELECT * FROM users");
while (rs.next()) {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setName(rs.getString("name"));
}
```

😩 Too much boilerplate, error-prone.

### ✅ With ORM (JPA + Hibernate)

```java
User user = userRepository.findById(1L).get();
```

### 🔑 Key Mapping Idea

| Java    | Database    |
| ------- | ----------- |
| Class   | Table       |
| Object  | Row         |
| Field   | Column      |
| @Entity | Marks table |
| @Id     | Primary key |

---

## 2️⃣ JPA vs Hibernate (Very Important)

| JPA                   | Hibernate               |
| --------------------- | ----------------------- |
| Specification (rules) | Implementation (engine) |
| Interface             | Actual code             |
| Vendor-independent    | Vendor-specific         |
| `@Entity`, `@Id`      | Executes SQL            |

👉 **Spring Boot uses Hibernate as JPA provider by default**

📌 Think like this:

> JPA = rules
> Hibernate = player following the rules

---

## 3️⃣ Entity Basics (Custom Entity)

### 🧱 What is an Entity?

An **Entity** is a Java class mapped to a database table.

---

### ✅ Simple Entity Example

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private Integer age;

    // getters & setters
}
```

### 🧠 What happens here?

* `@Entity` → Hibernate manages this class
* `@Table` → maps to DB table
* `@Id` → primary key
* `@GeneratedValue` → auto increment

---

## 4️⃣ Entity Lifecycle (Super Important for Interviews)

### 🌀 Entity States

```
NEW → MANAGED → DETACHED → REMOVED
```

---

### 1️⃣ New (Transient)

```java
User user = new User();
user.setName("Arashad");
```

❌ Not in DB
❌ Not tracked by Hibernate

---

### 2️⃣ Managed (Persistent)

```java
entityManager.persist(user);
```

✅ Hibernate tracks it
✅ Auto SQL generated

---

### 3️⃣ Detached

```java
entityManager.detach(user);
```

❌ No longer tracked
❌ Changes NOT saved

---

### 4️⃣ Removed

```java
entityManager.remove(user);
```

❌ Deleted from DB

---

### 🔥 Real-World Tip

> Most of the time, Spring Data JPA handles lifecycle automatically
> You rarely use `EntityManager` directly in projects

---

## 5️⃣ Repositories (Core of Persistence Layer)

### ❓ What is Repository?

Repository is a **data access layer** abstraction.

You **don’t write SQL** for common operations.

---

### ✅ Basic Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

### 🎁 What you get for FREE

* `save()`
* `findById()`
* `findAll()`
* `deleteById()`
* `count()`

---

## 6️⃣ Custom Repository Methods

### 🔍 Query Method (No SQL)

```java
List<User> findByAge(Integer age);
```

Spring generates:

```sql
SELECT * FROM users WHERE age = ?
```

---

### 🔍 Multiple Conditions

```java
Optional<User> findByEmailAndName(String email, String name);
```

---

### 🧠 Naming Rule

```
findBy + FieldName + Condition
```

Examples:

* `findByName`
* `findByAgeGreaterThan`
* `findByEmailContaining`

---

## 7️⃣ Custom Query (@Query)

### JPQL Example

```java
@Query("SELECT u FROM User u WHERE u.age > :age")
List<User> findUsersOlderThan(@Param("age") Integer age);
```

📌 JPQL uses **Entity names**, not table names.

---

### Native Query (When Needed)

```java
@Query(value = "SELECT * FROM users WHERE age > ?", nativeQuery = true)
List<User> findUsersNative(Integer age);
```

---

## 8️⃣ CRUD Operations (Real Usage)

### ➕ Create

```java
User user = new User();
user.setName("Arashad");
user.setEmail("a@gmail.com");
userRepository.save(user);
```

---

### 📖 Read

```java
User user = userRepository.findById(1L)
        .orElseThrow(() -> new RuntimeException("Not found"));
```

---

### ✏️ Update

```java
User user = userRepository.findById(1L).get();
user.setAge(25);
userRepository.save(user);
```

👉 Same `save()` works for **create & update**

---

### ❌ Delete

```java
userRepository.deleteById(1L);
```

---

## 9️⃣ CRUD Tests (Very Important)

### Why Tests?

* Catch DB issues early
* Verify mapping
* Safe refactoring

---

### ✅ Repository Test Example

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setName("Arashad");
        user.setEmail("test@gmail.com");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
    }
}
```

---

### 🧠 What @DataJpaTest does?

* Loads only JPA layer
* Uses in-memory DB (H2)
* Fast & isolated tests

---

## 🔟 Best Practices (Production Level)

### ✅ Do

✔ Use DTOs for API
✔ Keep entities clean
✔ Use pagination (`Pageable`)
✔ Avoid native queries unless needed

---

### ❌ Avoid

❌ Business logic inside entity
❌ Fetching everything eagerly
❌ Writing SQL for simple queries

---

## 🎯 Final Confidence Checklist

After today, you should be confident that:

* ✔ You understand ORM
* ✔ You know JPA vs Hibernate
* ✔ You can create entities
* ✔ You know entity lifecycle
* ✔ You can write repositories
* ✔ You can do CRUD without SQL
* ✔ You can test persistence layer

---

If you want, next I can:

* 🔥 Explain **Lazy vs Eager loading**
* 🔥 Show **real HMIS-style entity relationships**
* 🔥 Do **advanced repository patterns**
* 🔥 Connect **service → repository → DB flow**


