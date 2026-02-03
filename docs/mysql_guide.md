# ✅ Ways to Write MySQL Queries in Spring Boot — **Comparison Table**

| Aspect              | JPA Derived Queries | JPQL (`@Query`)     | Native SQL (`@Query`) | JdbcTemplate   | EntityManager (Native) |
| ------------------- | ------------------- | ------------------- | --------------------- | -------------- | ---------------------- |
| Query Type          | No SQL              | JPQL (Entity-based) | Pure MySQL SQL        | Pure MySQL SQL | Pure MySQL SQL         |
| Uses Entity?        | ✅ Yes               | ✅ Yes               | ⚠️ Optional           | ❌ No           | ❌ No                   |
| Uses Table Name?    | ❌ No                | ❌ No                | ✅ Yes                 | ✅ Yes          | ✅ Yes                  |
| Uses Column Name?   | ❌ No                | ❌ No                | ✅ Yes                 | ✅ Yes          | ✅ Yes                  |
| Boilerplate Code    | ⭐ Very Low          | ⭐ Low               | ⭐ Medium              | ⭐ High         | ⭐ High                 |
| Readability         | ⭐⭐⭐⭐                | ⭐⭐⭐                 | ⭐⭐                    | ⭐⭐             | ⭐                      |
| Performance Control | ⭐⭐                  | ⭐⭐                  | ⭐⭐⭐⭐                  | ⭐⭐⭐⭐           | ⭐⭐⭐⭐                   |
| Dynamic Queries     | ❌ Poor              | ❌ Poor              | ⚠️ Medium             | ✅ Good         | ✅ Excellent            |
| Pagination Support  | ✅ Built-in          | ✅ Built-in          | ⚠️ Manual / Page      | ❌ Manual       | ❌ Manual               |
| Type Safety         | ⭐⭐⭐⭐                | ⭐⭐⭐                 | ⭐⭐                    | ⭐              | ⭐                      |
| Learning Curve      | ⭐                   | ⭐⭐                  | ⭐⭐⭐                   | ⭐⭐⭐            | ⭐⭐⭐⭐                   |
| Production Usage    | ⭐⭐⭐⭐⭐               | ⭐⭐⭐⭐                | ⭐⭐⭐                   | ⭐⭐⭐            | ⭐⭐                     |

---

# 🔍 HOW EACH METHOD LOOKS (Quick View)

---

## 1️⃣ JPA Derived Queries (BEST DEFAULT)

```java
Patient findByPatientCode(String patientCode);
```

### ✅ Use when

* Simple CRUD
* Filters by columns
* Clean & fast development

### ❌ Avoid when

* Complex joins
* Reports

---

## 2️⃣ JPQL (`@Query` with Entity)

```java
@Query("SELECT p FROM Patient p WHERE p.gender = :gender")
List<Patient> findByGender(String gender);
```

### ✅ Use when

* Entity relationships
* DB-independent queries

### ❌ Avoid when

* DB-specific SQL
* Heavy reports

---

## 3️⃣ Native SQL (`@Query(nativeQuery = true)`)

```java
@Query(
  value = "SELECT * FROM patients WHERE gender = :gender",
  nativeQuery = true
)
List<Patient> findByGenderNative(String gender);
```

### ✅ Use when

* Performance-critical
* Legacy schema
* MySQL-specific features

### ❌ Avoid when

* Simple CRUD (overkill)

---

## 4️⃣ JdbcTemplate (LOW-LEVEL, FAST)

```java
jdbcTemplate.query(
  "SELECT * FROM patients WHERE gender = ?",
  new BeanPropertyRowMapper<>(Patient.class),
  gender
);
```

### ✅ Use when

* Reports
* Bulk operations
* Fine-grained control

### ❌ Avoid when

* Normal application CRUD

---

## 5️⃣ EntityManager (ADVANCED / RARE)

```java
entityManager
  .createNativeQuery(sql)
  .getResultList();
```

### ✅ Use when

* Dynamic SQL
* Stored procedures
* Very complex queries

### ❌ Avoid when

* Team readability matters

---

# 🔥 PRODUCTION BEST PRACTICE MATRIX

| Use Case                 | Best Choice                  |
| ------------------------ | ---------------------------- |
| Insert / Update / Delete | JPA Repository               |
| Simple Search APIs       | JPA Derived Queries          |
| Entity Joins             | JPQL                         |
| Reports / Dashboards     | Native SQL / JdbcTemplate    |
| Legacy DB                | Native SQL                   |
| Large Tables             | Native SQL + Index           |
| Dynamic Filters          | Specification / JdbcTemplate |
| Pagination APIs          | JPA / Native + Page          |

---

# 🚨 WHAT **NOT** TO DO (PRODUCTION MISTAKES)

| ❌ Mistake                   | Why it’s Bad                |
| --------------------------- | --------------------------- |
| SQL in Controller           | Breaks architecture         |
| `ddl-auto=update`           | Can destroy data            |
| Native SQL everywhere       | Hard to maintain            |
| Returning Entity in Reports | Memory & performance issues |
| No pagination               | DB overload                 |
| No indexes                  | Slow queries                |

---

# 🧠 FINAL DECISION RULE (MEMORIZE THIS)

```
Is it simple CRUD?
→ JPA

Is it entity-based logic?
→ JPQL

Is it performance / report / legacy?
→ Native SQL or JdbcTemplate
```

---

# 🏥 REAL HMIS / ENTERPRISE PRACTICE

* **80%** → JPA / JPQL
* **15%** → Native SQL
* **5%** → JdbcTemplate / EntityManager

That balance keeps:

* Code readable
* DB fast
* Production stable
