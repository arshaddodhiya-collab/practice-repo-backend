package com.test.practice.controller;

import com.test.practice.dto.PostDTO;
// import com.test.practice.entity.Post;
import com.test.practice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<PostDTO> createPost(@PathVariable Long userId, @Valid @RequestBody PostDTO postDTO) {
        PostDTO createdPost = postService.createPost(userId, postDTO);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        List<PostDTO> posts = postService.getPostsByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
