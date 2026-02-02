package com.test.practice.controller;

import com.test.practice.dto.PostDTO;
// import com.test.practice.entity.Post;
import com.test.practice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/posts")
public class PostController {

    @Autowired
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@PathVariable Long userId, @Valid @RequestBody PostDTO postDTO) {
        PostDTO createdPost = postService.createPost(userId, postDTO);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPostsByUserId(@PathVariable Long userId, Pageable pageable) {
        Page<PostDTO> posts = postService.getPostsByUserId(userId, pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
