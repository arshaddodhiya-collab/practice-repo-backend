package com.test.practice.service;

import com.test.practice.dto.CommentDTO;
import com.test.practice.entity.Comment;
import com.test.practice.entity.Post;
import com.test.practice.entity.User;
import com.test.practice.exception.ResourceNotFoundException;
import com.test.practice.repository.CommentRepository;
import com.test.practice.repository.PostRepository;
import com.test.practice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
            PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public CommentDTO addComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = new Comment(commentDTO.getText(), user, post);
        Comment savedComment = commentRepository.save(comment);

        return mapToDTO(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        // Using the HQL method we created
        List<CommentDTO> comments = commentRepository.findCommentsWithUserByPostId(postId);
        return comments;
    }

    private CommentDTO mapToDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getPost().getId(),
                comment.getCreatedAt());
    }
}
