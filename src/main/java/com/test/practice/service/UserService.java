package com.test.practice.service;

import com.test.practice.dto.PostDTO;
import com.test.practice.dto.UserDTO;
import com.test.practice.entity.Post;
import com.test.practice.entity.User;
import com.test.practice.exception.ResourceNotFoundException;
import com.test.practice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        Objects.requireNonNull(userDTO, "userDTO must not be null");

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());

        User savedUser = userRepository.save(user);
        logger.debug("Created user with id={}", savedUser.getId());
        return mapToDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserService::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.debug("Deleted user with id={}", id);
    }

    private static UserDTO mapToDTO(User user) {
        List<PostDTO> postDTOs = user.getPosts() != null
                ? user.getPosts().stream().map(UserService::mapToPostDTO).collect(Collectors.toList())
                : null;
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), postDTOs);
    }

    private static PostDTO mapToPostDTO(Post post) {
        return new PostDTO(post.getId(), post.getTitle(), post.getContent());
    }
}
