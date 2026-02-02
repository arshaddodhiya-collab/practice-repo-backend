package com.test.practice.service;

import com.test.practice.dto.PostDTO;
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

    public PostDTO createPost(Long userId, PostDTO postDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        return mapToDTO(savedPost);
    }

    public List<PostDTO> getPostsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return postRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private PostDTO mapToDTO(Post post) {
        return new PostDTO(post.getId(), post.getTitle(), post.getContent());
    }
}
