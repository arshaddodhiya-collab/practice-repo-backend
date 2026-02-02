package com.test.practice.service;

import com.test.practice.dto.PostDTO;
import com.test.practice.dto.UserDTO;
import com.test.practice.entity.Post;
import com.test.practice.entity.User;
import com.test.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private PostDTO mapToPostDTO(Post post) {
        return new PostDTO(post.getId(), post.getTitle(), post.getContent());
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.test.practice.exception.ResourceNotFoundException(
                        "User not found with id: " + id));
        return mapToDTO(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new com.test.practice.exception.ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(User user) {
        List<PostDTO> postDTOs = user.getPosts() != null
                ? user.getPosts().stream().map(this::mapToPostDTO).collect(Collectors.toList())
                : null;
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), postDTOs);
    }
}
