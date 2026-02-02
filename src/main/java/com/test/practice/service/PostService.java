package com.test.practice.service;

import com.test.practice.entity.Post;
import com.test.practice.entity.User;
import com.test.practice.exception.ResourceNotFoundException;
import com.test.practice.repository.PostRepository;
import com.test.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(Long userId, Post post) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        post.setUser(user);
        return postRepository.save(post);
    }

    public List<Post> getPostsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return postRepository.findByUserId(userId);
    }
}
