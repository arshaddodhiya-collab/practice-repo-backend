# ✅ Native Queries in Spring Boot (DEEP EXPLANATION)

---

## 🧠 What is a Native Query?

> A **Native Query** is a query where **YOU write pure MySQL SQL**,
> and **Hibernate just executes it** and maps the result.

👉 Spring Data **does NOT parse method names**
👉 Hibernate **does NOT generate SQL**
👉 MySQL SQL is executed **as-is**

---

## 🔁 Compare with Derived Queries (1 line)

| Derived Query     | Native Query             |
| ----------------- | ------------------------ |
| Method name → SQL | SQL written by developer |
| Parser-based      | No parsing               |
| Limited power     | Full SQL power           |
| DB independent    | DB specific              |

---

# 1️⃣ BASIC NATIVE QUERY (MOST COMMON)

### Repository method

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {

  @Query(
    value = "SELECT * FROM patients WHERE age > :age",
    nativeQuery = true
  )
  List<Patient> findPatientsOlderThan(@Param("age") int age);
}
```

### What happens internally

1. Spring sees `nativeQuery = true`
2. Spring **skips method-name parsing**
3. Hibernate sends SQL directly to MySQL
4. Result set is returned
5. Hibernate maps rows → `Patient` entity

---

# 2️⃣ HOW HIBERNATE MAPS RESULT TO ENTITY

### Rule (VERY IMPORTANT)

> Column names in SQL **must match entity column mapping**

Entity:

```java
@Column(name = "patient_code")
private String patientCode;
```

SQL must return:

```sql
patient_code
```

If you rename columns in SQL → mapping breaks.

---

# 3️⃣ USING NAMED PARAMETERS (BEST PRACTICE)

❌ Bad (SQL injection risk):

```java
"SELECT * FROM patients WHERE age > " + age
```

✅ Good:

```java
WHERE age > :age
```

Hibernate:

* Safely binds values
* Prevents SQL injection

---

# 4️⃣ NATIVE QUERY RETURNING DTO (PRODUCTION BEST PRACTICE)

🚨 **For reports, NEVER return entities**

### Option A: Interface-based DTO (BEST)

```java
public interface PatientSummaryDTO {
  Long getId();
  String getPatientCode();
  Integer getAge();
}
```

```java
@Query(
  value = """
    SELECT 
      id AS id,
      patient_code AS patientCode,
      age AS age
    FROM patients
    WHERE gender = :gender
  """,
  nativeQuery = true
)
List<PatientSummaryDTO> findPatientSummary(@Param("gender") String gender);
```

Why this works:

* Column aliases must match DTO getter names
* Faster
* No lazy-loading issues

---

## Option B: `Object[]` (NOT RECOMMENDED)

```java
List<Object[]> result;
```

❌ Hard to maintain
❌ Error-prone
Only acceptable for quick internal tools

---

# 5️⃣ NATIVE QUERY WITH PAGINATION (PRODUCTION MUST)

```java
@Query(
  value = """
    SELECT *
    FROM patients
    WHERE gender = :gender
  """,
  countQuery = """
    SELECT COUNT(*)
    FROM patients
    WHERE gender = :gender
  """,
  nativeQuery = true
)
Page<Patient> findPatientsByGender(
  @Param("gender") String gender,
  Pageable pageable
);
```

Why countQuery is needed:

* Spring cannot auto-generate COUNT for native SQL

---

# 6️⃣ NATIVE INSERT / UPDATE / DELETE

### Insert

```java
@Modifying
@Transactional
@Query(
  value = """
    INSERT INTO patients (patient_code, age)
    VALUES (:code, :age)
  """,
  nativeQuery = true
)
void insertPatient(@Param("code") String code, @Param("age") int age);
```

---

### Update

```java
@Modifying
@Transactional
@Query(
  value = """
    UPDATE patients
    SET age = :age
    WHERE id = :id
  """,
  nativeQuery = true
)
int updateAge(@Param("id") Long id, @Param("age") int age);
```

✔ Returns affected row count

---

### Delete

```java
@Modifying
@Transactional
@Query(
  value = "DELETE FROM patients WHERE id = :id",
  nativeQuery = true
)
void deletePatientNative(@Param("id") Long id);
```

---

# 7️⃣ NATIVE QUERY USING EntityManager (ADVANCED)

Used when:

* Dynamic SQL
* Conditional joins
* Stored procedures

```java
String sql = """
  SELECT p.id, COUNT(v.id)
  FROM patients p
  JOIN visits v ON v.patient_id = p.id
  GROUP BY p.id
""";

List<Object[]> result =
  entityManager.createNativeQuery(sql).getResultList();
```

⚠️ Harder to maintain
✔ Maximum flexibility

---

# 8️⃣ TRANSACTIONS WITH NATIVE QUERIES

Native queries **still use Spring transactions**.

```java
@Transactional
public void updatePatientData() {
  repo.updateAge(1L, 40);
  repo.updateMobile(1L, "9999999999");
}
```

✔ Commit together
✔ Rollback on failure

---

# 9️⃣ COMMON PRODUCTION MISTAKES

❌ Using native queries everywhere
❌ Returning entities for reports
❌ No pagination
❌ No indexes
❌ Writing native SQL in controller
❌ Forgetting `@Modifying` for updates

---

# 🔥 WHEN TO USE NATIVE QUERIES (CLEAR RULES)

Use native queries when:

* Complex joins
* Reports & dashboards
* Performance-critical queries
* Legacy schemas
* DB-specific functions (JSON, window functions)

Avoid when:

* Simple CRUD
* Basic filters
* Rapid development

---

# 🧠 FINAL MENTAL MODEL

```
Derived Query → Method name → Parser → SQL
Native Query  → Your SQL → Hibernate → DB
```

Hibernate’s role in native queries:

* Bind parameters
* Execute SQL
* Map result

**Nothing else.**

---

# 🎯 FINAL TAKEAWAY

> Native queries give you **power and responsibility**.

* ✔ Full control
* ✔ Full performance
* ❌ Less safety
* ❌ More maintenance

That’s why production systems use **native queries selectively**, not blindly.

