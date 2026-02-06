package com.test.practice.repository;

import com.test.practice.entity.Post;
import com.test.practice.projection.PostView;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository
        extends JpaRepository<Post, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Post> {
    Page<PostView> findByUserId(Long userId, Pageable pageable);

    List<PostView> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = { "user", "comments" })
    Optional<Post> findWithUserAndCommentsById(Long id);

    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    List<Post> findAllWithUserFetchJoin();
}
