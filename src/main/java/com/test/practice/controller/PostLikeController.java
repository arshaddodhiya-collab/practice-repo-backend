package com.test.practice.controller;

import com.test.practice.dto.PostLikeDTO;
import com.test.practice.service.PostLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping
    public ResponseEntity<Void> likePost(@RequestBody PostLikeDTO likeDTO) {
        postLikeService.likePost(likeDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, @PathVariable Long userId) {
        postLikeService.unlikePost(userId, postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(postLikeService.countLikes(postId));
    }
}
