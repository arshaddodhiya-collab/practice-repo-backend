package com.test.practice.repository;

import com.test.practice.entity.Category;
import com.test.practice.projection.CategoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<CategoryView> findByName(String name);
}
