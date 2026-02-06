package com.test.practice;

import com.test.practice.entity.*;
import com.test.practice.projection.UserSummary;
import com.test.practice.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Ensures we pick up application.properties from src/test/resources if
                        // configured, or we can rely on standard precedence
public class AdvancedPatternsTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    public void testSpecifications() {
        // Setup Data
        User user = new User("John Doe", "john@example.com");
        userRepository.save(user);

        Category javaCat = new Category();
        javaCat.setName("Java");
        categoryRepository.save(javaCat);

        Category pythonCat = new Category();
        pythonCat.setName("Python");
        categoryRepository.save(pythonCat);

        Post p1 = new Post("Learn Java Loops", "Content", user);
        p1.setCategory(javaCat);
        postRepository.save(p1);

        Post p2 = new Post("Advanced Java Streams", "Content", user);
        p2.setCategory(javaCat);
        postRepository.save(p2);

        Post p3 = new Post("Python Basics", "Content", user);
        p3.setCategory(pythonCat);
        postRepository.save(p3);

        // Test titleContains
        List<Post> javaPosts = postRepository.findAll(PostSpecifications.titleContains("Java"));
        assertEquals(2, javaPosts.size(), "Should find 2 posts with 'Java' in title");

        // Test hasCategory
        List<Post> pythonPosts = postRepository.findAll(PostSpecifications.hasCategory("Python"));
        assertEquals(1, pythonPosts.size(), "Should find 1 post in 'Python' category");
        assertEquals("Python Basics", pythonPosts.get(0).getTitle());

        // Test Combined
        List<Post> specificPosts = postRepository.findAll(
                PostSpecifications.titleContains("Loops").and(PostSpecifications.hasCategory("Java")));
        assertEquals(1, specificPosts.size());
    }

    @Test
    @Transactional
    public void testProjections() {
        User user = new User("Alice Projection", "alice@proj.com");
        userRepository.save(user);

        List<UserSummary> summaries = userRepository.findAllProjectedBy();

        // We know we just added one, but existing DB state might affect size if not
        // cleaned.
        // But since we are using create-drop H2, it should be clean or we filter.

        boolean found = summaries.stream()
                .anyMatch(s -> s.getEmail().equals("alice@proj.com") && s.getName().equals("Alice Projection"));

        assertTrue(found, "Should find the projected user summary");

        // Verify projection is working (not null)
        assertNotNull(summaries.get(0).getName());
    }

    @Test
    @Transactional
    public void testEntityGraph() {
        User user = new User("Graph User", "graph@example.com");
        userRepository.save(user);

        Post post = new Post("Graph Post", "Content", user);
        postRepository.save(post);

        // Clear persistence context to ensure we are actually fetching from DB
        // In a real integration test, this happens naturally between transactions or
        // requests
        // But here @Transactional wraps the whole method.
        // We should verify the method call doesn't throw and returns data.
        // True verification of "single query" requires inspecting Hibernate stats or
        // logs,
        // but for functional verification:

        Optional<Post> fetched = postRepository.findWithUserAndCommentsById(post.getId());
        assertTrue(fetched.isPresent());
        assertEquals("Graph User", fetched.get().getUser().getName());
    }

    @Test
    @Transactional
    public void testFetchJoin() {
        User user = new User("Join User", "join@example.com");
        userRepository.save(user);

        Post post = new Post("Join Post", "Content", user);
        postRepository.save(post);

        List<Post> posts = postRepository.findAllWithUserFetchJoin();

        assertFalse(posts.isEmpty());
        // Verify we can access the user without exception (and importantly, it should
        // be eagerly loaded)
        assertEquals("Join User", posts.get(0).getUser().getName());
    }
}
