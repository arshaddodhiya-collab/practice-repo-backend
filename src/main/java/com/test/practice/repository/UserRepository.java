package com.test.practice.repository;

import com.test.practice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    java.util.List<UserSummary> findAllProjectedBy();
}