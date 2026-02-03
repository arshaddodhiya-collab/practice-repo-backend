package com.test.practice.repository;

import com.test.practice.dto.UserActivityReportDTO;
import com.test.practice.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // JPA Derived Query
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // Native Query: Count likes for a post
    @Query(value = "SELECT COUNT(*) FROM post_likes WHERE post_id = :postId", nativeQuery = true)
    Long countLikesByPostId(@Param("postId") Long postId);

    // Native Query: Report - Top 5 active users (Commented + Liked)
    // demonstrates complex native query mapping to Interface-based DTO
    @Query(value = """
            SELECT
                u.id AS userId,
                u.name AS userName,
                (COUNT(DISTINCT c.id) + COUNT(DISTINCT pl.id)) AS activityCount
            FROM users u
            LEFT JOIN comments c ON u.id = c.user_id
            LEFT JOIN post_likes pl ON u.id = pl.user_id
            GROUP BY u.id
            ORDER BY activityCount DESC
            LIMIT 5
            """, nativeQuery = true)
    List<UserActivityReportDTO> findTopActiveUsers();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_likes WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    void deleteLikeNative(@Param("userId") Long userId, @Param("postId") Long postId);
}
