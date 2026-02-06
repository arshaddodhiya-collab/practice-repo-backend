package com.test.practice.service;

import com.test.practice.dto.CategoryDTO;
import com.test.practice.dto.PostDTO;
import com.test.practice.entity.Category;
// import com.test.practice.entity.Post;
import com.test.practice.projection.PostView;
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
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build();
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
        List<PostView> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream()
                .map(this::mapToPostDTO)
                .collect(Collectors.toList());
    }

    private PostDTO mapToPostDTO(PostView post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryId(post.getCategory() != null ? post.getCategory().getId() : null)
                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                .build();
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
