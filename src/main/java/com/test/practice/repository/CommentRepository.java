package com.test.practice.repository;

import com.test.practice.dto.CommentDTO;
import com.test.practice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // JPA Derived Query
    List<CommentDTO> findByPostId(Long postId);

    // HQL Query with JOIN FETCH to optimize performance (avoid N+1)
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<CommentDTO> findCommentsWithUserByPostId(@Param("postId") Long postId);
}
