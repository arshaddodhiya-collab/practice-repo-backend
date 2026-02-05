# Advanced Repository Patterns Walkthrough

We have upgraded the repository layer to use professional-grade patterns that solve common performance and maintainability issues.

## 1. Dynamic Filtering with Specifications
**Problem:** You need to filter Posts by title, date, author, category, etc., in various combinations. Writing a method for every combination (`findByTitle`, `findByTitleAndCategory`, etc.) is unmaintainable.
**Solution:** `JpaSpecificationExecutor` + [Specification](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/PostSpecifications.java#6-26) classes.

### Changes
- **[PostSpecifications.java](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/PostSpecifications.java)**: Contains reusable filter logic.
    - [titleContains("Java")](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/PostSpecifications.java#8-16)
    - [hasCategory("Spring")](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/PostSpecifications.java#17-25)
- **[PostRepository.java](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/PostRepository.java)**: Now interacts with these specifications.

**Usage Example:**
```java
postRepository.findAll(
    PostSpecifications.titleContains("Java").and(PostSpecifications.hasCategory("Spring"))
);
```

## 2. Solving N+1 with Entity Graphs
**Problem:** `fetch=Lazy` is good, but when you *do* need the data (e.g., displaying a post with its author), Hibernate runs 1 query for the Post and then N queries for the Users.
**Solution:** `@EntityGraph`. It tells Hibernate: "For this specific method, perform a LEFT JOIN and fetch everything in one go."

### Changes
- **[PostRepository.java](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/PostRepository.java)**:
```java
@EntityGraph(attributePaths = {"user", "comments"})
Optional<Post> findWithUserAndCommentsById(Long id);
```

## 3. High Performance with Projections
**Problem:** You just want to display a list of User names. Fetching the entire [User](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/entity/User.java#9-89) entity also drags along their email, profile settings, and potentially lazy lists, consuming memory.
**Solution:** Interface-based Projections. Fetch only what you need.

### Changes
- **[UserSummary.java](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/UserSummary.java)**: An interface defining only the fields you want ([getId](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/entity/User.java#41-44), [getName](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/UserSummary.java#6-7)).
- **[UserRepository.java](file:///home/artem/test/practice-backend/src/main/java/com/test/practice/repository/UserRepository.java)**:
```java
List<UserSummary> findAllProjectedBy();
```
*Hibernate generates a specific SQL query selecting only these columns.*

## 4. Verification
Created **[AdvancedPatternsTest.java](file:///home/artem/test/practice-backend/src/test/java/com/test/practice/AdvancedPatternsTest.java)** to verify these patterns work correctly using an in-memory H2 database.
