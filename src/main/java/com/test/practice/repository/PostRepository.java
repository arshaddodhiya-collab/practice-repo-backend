package com.test.practice.repository;

import com.test.practice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);

    List<Post> findByCategoryId(Long categoryId);
}
