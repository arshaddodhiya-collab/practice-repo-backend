In a complex system like an HMIS, standard `JpaRepository` methods quickly become insufficient. You need patterns that handle performance (N+1 problems), dynamic searching, and clean separation of concerns.

Here are the advanced repository patterns used in professional-grade Spring Boot applications.

---

## 1. The Specification Pattern (Dynamic Querying)

In an HMIS, you might need to filter patients by age, gender, blood type, and last visit date—all at once. Instead of writing 20 different repository methods, use **Specifications** based on the Criteria API.

**The Repository:**

```java
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    // Inherits findBy(Specification)
}

```

**The Specification Factory:**

```java
public class PatientSpecifications {
    public static Specification<Patient> hasBloodType(String bloodType) {
        return (root, query, cb) -> cb.equal(root.get("bloodType"), bloodType);
    }

    public static Specification<Patient> visitedAfter(LocalDateTime date) {
        return (root, query, cb) -> cb.greaterThan(root.get("lastVisit"), date);
    }
}

```

**Usage:** `repo.findAll(hasBloodType("O+").and(visitedAfter(lastMonth)));`

---

## 2. Custom Repository Implementation

Sometimes you need to drop down to raw `EntityManager` for complex logic or specific Hibernate features (like `UNWRAP` or native batch inserts) that Spring Data doesn't expose.

1. **Define an Interface:**

```java
public interface CustomEncounterRepository {
    void bulkUpdateStatus(List<Long> ids, String status);
}

```

2. **Implement it (Naming matters: `Impl` suffix is default):**

```java
@Repository
public class CustomEncounterRepositoryImpl implements CustomEncounterRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void bulkUpdateStatus(List<Long> ids, String status) {
        entityManager.createQuery("UPDATE Encounter e SET e.status = :status WHERE e.id IN :ids")
                     .setParameter("status", status)
                     .setParameter("ids", ids)
                     .executeUpdate();
    }
}

```

3. **Merge it:** `public interface EncounterRepository extends JpaRepository<Encounter, Long>, CustomEncounterRepository {}`

---

## 3. Projection Pattern (Performance)

Fetching an entire `Patient` entity (with its LOBs, addresses, and lists) just to show a "Name and ID" dropdown is a performance killer. **Projections** allow you to fetch only the columns you need.

**Interface-based Projection:**

```java
public interface PatientSummary {
    Long getId();
    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
    String getMrn(); // Medical Record Number
}

```

**Repository:**

```java
List<PatientSummary> findAllByFacilityId(Long facilityId);

```

*JPA will generate a specific SQL `SELECT id, first_name, last_name, mrn...` instead of `SELECT *`.*

---

## 4. Entity Graph Pattern (Solving N+1)

If you need to fetch an `Encounter` and its `Observations` in one trip without using `EAGER` fetching (which is evil), use `@EntityGraph`.

```java
public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    @EntityGraph(attributePaths = {"observations", "practitioner"})
    Optional<Encounter> findWithDetailsById(Long id);
}

```

This forces a **LEFT JOIN** in a single query, preventing the database from being hammered by  additional queries for the observations.

---

## 5. Summary Table

| Pattern | Best For... | Benefit |
| --- | --- | --- |
| **Specifications** | Complex search filters | Reusable, type-safe, dynamic. |
| **Projections** | Read-only views/UI Tables | Reduced memory and IO. |
| **Entity Graphs** | Loading relationships | Prevents N+1 performance issues. |
| **Custom Impl** | Complex bulk logic / Native SQL | Full control over the `EntityManager`. |
