package com.test.practice.dto;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long id;
    private String text;
    private Long userId;
    private String userName;
    private Long postId;
    private LocalDateTime createdAt;

    public CommentDTO() {
    }

    public CommentDTO(Long id, String text, Long userId, String userName, Long postId, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
