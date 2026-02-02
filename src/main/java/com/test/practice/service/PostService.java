package com.test.practice.service;

import com.test.practice.dto.PostDTO;
import com.test.practice.entity.Post;
import com.test.practice.entity.User;
import com.test.practice.exception.ResourceNotFoundException;
import com.test.practice.repository.PostRepository;
import com.test.practice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Constructor injection (preferred over field injection)
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = Objects.requireNonNull(postRepository, "postRepository must not be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
    }

    /**
     * Create a Post for an existing user.
     */
    @Transactional
    public PostDTO createPost(Long userId, PostDTO postDTO) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(postDTO, "postDTO must not be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        logger.debug("Created post with id={} for userId={}", savedPost.getId(), userId);
        return mapToDTO(savedPost);
    }

    /**
     * Retrieve posts for a user with pagination.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByUserId(Long userId, Pageable pageable) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(pageable, "pageable must not be null");

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return postRepository.findByUserId(userId, pageable)
                .map(PostService::mapToDTO);
    }

    private static PostDTO mapToDTO(Post post) {
        if (post == null) {
            return null;
        }
        return new PostDTO(post.getId(), post.getTitle(), post.getContent());
    }
}
