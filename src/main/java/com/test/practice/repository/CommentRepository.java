package com.test.practice.repository;

import com.test.practice.entity.Comment;
import com.test.practice.projection.CommentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // JPA Derived Query
    List<CommentView> findByPostId(Long postId);

    // HQL Query with JOIN FETCH to optimize performance (avoid N+1)
    // Note: When using Interface Projections with custom @Query, ensure aliases
    // match getters if not standard
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<CommentView> findCommentsWithUserByPostId(@Param("postId") Long postId);
}
