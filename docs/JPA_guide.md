# ✅ JPA Derived Queries — IN DETAIL

(**Spring Boot + Hibernate + MySQL**)

---

## 🧠 What are JPA Derived Queries?

> **JPA Derived Queries** are repository methods where
> **Spring Data JPA generates SQL automatically from the method name**.

You **don’t write SQL**
You **don’t write JPQL**
You only write **method names**

---

## 1️⃣ BASIC SETUP (Foundation)

### Entity

```java
@Entity
@Table(name = "patients")
public class Patient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String patientCode;
  private String firstName;
  private String lastName;
  private String gender;
  private Integer age;
  private String mobile;
  private Boolean active;
}
```

---

### Repository

```java
public interface PatientRepository
        extends JpaRepository<Patient, Long> {
}
```

This alone gives you:

```java
save()
findById()
findAll()
deleteById()
count()
existsById()
```

---

## 2️⃣ SIMPLE DERIVED QUERIES

### Find by one field

```java
Patient findByPatientCode(String patientCode);
```

Generated SQL:

```sql
SELECT * FROM patients WHERE patient_code = ?;
```

---

### Find by multiple fields (AND)

```java
List<Patient> findByGenderAndActive(String gender, Boolean active);
```

```sql
SELECT * FROM patients
WHERE gender = ? AND active = ?;
```

---

### OR condition

```java
List<Patient> findByMobileOrPatientCode(String mobile, String patientCode);
```

---

## 3️⃣ COMPARISON OPERATORS

### Greater / Less than

```java
List<Patient> findByAgeGreaterThan(Integer age);
List<Patient> findByAgeLessThan(Integer age);
```

---

### Between

```java
List<Patient> findByAgeBetween(Integer min, Integer max);
```

---

### Like / Contains / StartsWith

```java
List<Patient> findByFirstNameLike(String name);        // %name%
List<Patient> findByFirstNameContaining(String name); // %name%
List<Patient> findByFirstNameStartingWith(String name); // name%
List<Patient> findByFirstNameEndingWith(String name);   // %name
```

---

## 4️⃣ NULL / BOOLEAN CHECKS

### IsNull / IsNotNull

```java
List<Patient> findByMobileIsNull();
List<Patient> findByMobileIsNotNull();
```

---

### Boolean fields

```java
List<Patient> findByActiveTrue();
List<Patient> findByActiveFalse();
```

---

## 5️⃣ IN, NOT IN

```java
List<Patient> findByGenderIn(List<String> genders);
```

```sql
SELECT * FROM patients WHERE gender IN (?, ?, ?);
```

---

## 6️⃣ COUNT / EXISTS (VERY IMPORTANT)

### Exists (FAST)

```java
boolean existsByMobile(String mobile);
```

Generated SQL:

```sql
SELECT 1 FROM patients WHERE mobile = ? LIMIT 1;
```

🔥 **Best way to check duplicates**

---

### Count

```java
long countByGender(String gender);
```

---

## 7️⃣ ORDER BY

```java
List<Patient> findByGenderOrderByAgeDesc(String gender);
```

```sql
SELECT * FROM patients
WHERE gender = ?
ORDER BY age DESC;
```

---

## 8️⃣ TOP / FIRST (LIMIT)

```java
Patient findTopByOrderByIdDesc();
List<Patient> findFirst5ByGender(String gender);
```

```sql
SELECT * FROM patients
ORDER BY id DESC
LIMIT 1;
```

---

## 9️⃣ PAGINATION (PRODUCTION MUST)

```java
Page<Patient> findByGender(String gender, Pageable pageable);
```

Usage:

```java
PageRequest.of(0, 20);
```

Generated SQL:

```sql
LIMIT 20 OFFSET 0
```

🔥 **Always paginate in production**

---

## 🔟 OPTIONAL RETURN TYPE (BEST PRACTICE)

```java
Optional<Patient> findByPatientCode(String patientCode);
```

Why?

* Prevents `NullPointerException`
* Forces proper handling

---

## 1️⃣1️⃣ IGNORE CASE

```java
List<Patient> findByFirstNameIgnoreCase(String name);
```

---

## 1️⃣2️⃣ DISTINCT

```java
List<Patient> findDistinctByGender(String gender);
```

---

## 1️⃣3️⃣ NESTED / RELATIONSHIP QUERIES

Example:

```java
class Visit {
  @ManyToOne
  private Patient patient;
}
```

```java
List<Visit> findByPatientPatientCode(String code);
```

Spring navigates relationships automatically 🔥

---

## 1️⃣4️⃣ WHAT YOU CANNOT DO WITH DERIVED QUERIES

❌ Complex joins
❌ Subqueries
❌ GROUP BY
❌ Window functions
❌ Performance tuning

➡️ Use **JPQL / Native SQL** there

---

## 🔥 PRODUCTION BEST PRACTICES

### ✅ DO

✔ Use `Optional`
✔ Use pagination
✔ Index DB columns
✔ Keep method names readable
✔ Prefer `existsBy()` over `findBy()` for checks

---

### ❌ DON’T

❌ Overload long method names
❌ Use for reporting queries
❌ Use without indexes on large tables

---

## 🧠 REAL PRODUCTION RULE

```
If the method name becomes unreadable →
STOP → use @Query or Native SQL
```

Example ❌:

```java
findByGenderAndAgeGreaterThanAndActiveTrueAndMobileIsNotNullOrderByAgeDesc
```

---

## 🎯 FINAL SUMMARY

| Aspect         | JPA Derived Queries |
| -------------- | ------------------- |
| SQL writing    | ❌ No                |
| Learning curve | ⭐ Low               |
| Performance    | ⭐⭐⭐                 |
| Production use | ⭐⭐⭐⭐⭐               |
| Best for       | CRUD + filters      |

---

## 🏁 FINAL MENTAL MODEL

> **Method name = Query**
> **Entity field = Column**
> **Repository = DB access layer**

