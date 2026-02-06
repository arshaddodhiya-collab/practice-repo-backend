package com.test.practice.service;

import com.test.practice.dto.CategoryDTO;
import com.test.practice.dto.PostDTO;
import com.test.practice.entity.Category;
import com.test.practice.entity.Post;
import com.test.practice.exception.ResourceNotFoundException;
import com.test.practice.repository.CategoryRepository;
import com.test.practice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category(categoryDTO.getName(), categoryDTO.getDescription());
        Category savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id " + categoryId);
        }
        List<PostDTO> posts = postRepository.findByCategoryId(categoryId);
        return posts;
    }

    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }

    private PostDTO mapToPostDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory() != null ? post.getCategory().getId() : null,
                post.getCategory() != null ? post.getCategory().getName() : null);
    }
}
