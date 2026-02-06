package com.test.practice.repository;

import com.test.practice.dto.UserSummary;
import com.test.practice.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<UserSummary> findAllProjectedBy();
}