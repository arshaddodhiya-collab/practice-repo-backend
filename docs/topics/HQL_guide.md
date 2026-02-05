# ✅ HQL (Hibernate Query Language) — FULL EXPLANATION

---

## 🧠 What is HQL?

> **HQL is an object-oriented query language**
> where you write queries using **Entity names and fields**,
> **NOT table names and columns**.

👉 Looks like SQL
👉 Works on **Java objects**
👉 Hibernate converts it → SQL

---

## 🔁 Position in the Query World

```
Derived Query  → No query written
HQL / JPQL     → Entity-based query
Native SQL    → Table-based query
```

HQL sits **in the middle**.

---

# 1️⃣ SIMPLE HQL EXAMPLE

### Entity

```java
@Entity
@Table(name = "patients")
public class Patient {
  private Long id;
  private String gender;
  private Integer age;
}
```

---

### HQL Query

```java
@Query("FROM Patient p WHERE p.age > :age")
List<Patient> findOlderPatients(@Param("age") int age);
```

### Hibernate converts this to SQL

```sql
SELECT *
FROM patients
WHERE age > ?
```

✔ You never mention `patients`
✔ You never mention column names

---

# 2️⃣ HQL SYNTAX RULES (VERY IMPORTANT)

### ❌ WRONG (table + column)

```java
FROM patients WHERE age > 30
```

### ✅ CORRECT (entity + field)

```java
FROM Patient WHERE age > 30
```

---

## 🔑 Key Rule

```
HQL → Java world
SQL → Database world
```

---

# 3️⃣ SELECT CLAUSE IN HQL

### Full select

```java
@Query("SELECT p FROM Patient p")
List<Patient> findAllPatients();
```

### Partial select (DTO)

```java
@Query("""
  SELECT new com.app.dto.PatientDTO(p.id, p.gender)
  FROM Patient p
""")
List<PatientDTO> findPatientDTOs();
```

🔥 **This is NOT possible with derived queries**

---

# 4️⃣ WHERE CONDITIONS (HQL)

### AND / OR

```java
@Query("""
  FROM Patient p
  WHERE p.gender = :gender AND p.age > :age
""")
List<Patient> findFilteredPatients(String gender, int age);
```

---

### BETWEEN

```java
FROM Patient p WHERE p.age BETWEEN :min AND :max
```

---

### LIKE

```java
FROM Patient p WHERE p.firstName LIKE %:name%
```

---

# 5️⃣ JOINS (WHY HQL IS POWERFUL)

### Entity Relationship

```java
class Visit {
  @ManyToOne
  private Patient patient;
}
```

---

### HQL Join

```java
@Query("""
  SELECT v
  FROM Visit v
  JOIN v.patient p
  WHERE p.gender = :gender
""")
List<Visit> findVisitsByGender(String gender);
```

🔥 No foreign key
🔥 No join condition
Hibernate knows relationships.

---

# 6️⃣ FETCH JOIN (SOLVES N+1 PROBLEM)

```java
@Query("""
  SELECT p
  FROM Patient p
  JOIN FETCH p.visits
""")
List<Patient> findPatientsWithVisits();
```

Hibernate:

* Fetches everything in **one SQL**
* Avoids lazy-loading explosion

---

# 7️⃣ AGGREGATION (COUNT, SUM, etc.)

```java
@Query("SELECT COUNT(p) FROM Patient p WHERE p.gender = :gender")
long countByGender(String gender);
```

---

# 8️⃣ UPDATE & DELETE (HQL)

### Update

```java
@Modifying
@Transactional
@Query("""
  UPDATE Patient p
  SET p.age = :age
  WHERE p.id = :id
""")
int updateAge(Long id, int age);
```

### Delete

```java
@Modifying
@Transactional
@Query("DELETE FROM Patient p WHERE p.active = false")
int deleteInactivePatients();
```

⚠️ These **bypass Hibernate cache**
Use carefully.

---

# 9️⃣ PAGINATION WITH HQL (BUILT-IN)

```java
@Query("FROM Patient p WHERE p.gender = :gender")
Page<Patient> findByGender(String gender, Pageable pageable);
```

Hibernate automatically:

* Applies `LIMIT`
* Applies `OFFSET`

---

# 🔍 HOW HQL WORKS INTERNALLY

1. Hibernate parses HQL
2. Converts HQL → JPQL AST
3. Translates to DB-specific SQL
4. Executes via JDBC
5. Maps result → entities/DTOs

So:

```
HQL → SQL → MySQL
```

---

# 1️⃣0️⃣ HQL vs JPQL (IMPORTANT)

| Aspect      | HQL            | JPQL     |
| ----------- | -------------- | -------- |
| Owner       | Hibernate      | JPA spec |
| Scope       | Hibernate-only | Standard |
| Features    | More powerful  | Limited  |
| Portability | ❌ Less         | ✅ More   |

📌 **In Spring Boot**
You mostly write **JPQL-style HQL**
People just say “HQL” casually.

---

# 1️⃣1️⃣ WHEN TO USE HQL (PRODUCTION RULES)

### ✅ Use HQL when:

* You need joins via entities
* You want DB independence
* You need fetch joins
* Derived query names are ugly

### ❌ Avoid HQL when:

* Query is DB-specific
* Heavy reporting
* Window functions needed

➡️ Use **Native SQL** there.

---

# 🚨 COMMON MISTAKES

❌ Using table names
❌ Using column names
❌ Forgetting `@Modifying`
❌ Returning entities for reports
❌ Ignoring fetch joins

---

# 🧠 FINAL MENTAL MODEL

```
Derived Query → No SQL, name-based
HQL / JPQL   → Entity-based query
Native SQL   → Database-based query
```

---

# 🎯 FINAL TAKEAWAY

> **HQL lets you think in Java, not SQL**
> while still giving you **control and clarity**.

That’s why production systems usually look like:

* **Derived Queries** for CRUD
* **HQL** for joins & business queries
* **Native SQL** for reports & performance
