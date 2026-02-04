package com.test.practice.dto;

import jakarta.validation.constraints.NotBlank;

public class PostDTO {
    private Long id;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Content must not be blank")
    private String content;

    private Long categoryId;
    private String categoryName;

    public PostDTO() {
    }

    public PostDTO(Long id, String title, String content) {
        this(id, title, content, null, null);
    }

    public PostDTO(Long id, String title, String content, Long categoryId, String categoryName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
