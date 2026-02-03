package com.test.practice.service;

import com.test.practice.dto.PostLikeDTO;
import com.test.practice.dto.UserActivityReportDTO;
import com.test.practice.entity.Post;
import com.test.practice.entity.PostLike;
import com.test.practice.entity.User;
import com.test.practice.exception.ResourceNotFoundException;
import com.test.practice.repository.PostLikeRepository;
import com.test.practice.repository.PostRepository;
import com.test.practice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, UserRepository userRepository,
            PostRepository postRepository) {
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void likePost(PostLikeDTO likeDTO) {
        if (postLikeRepository.existsByUserIdAndPostId(likeDTO.getUserId(), likeDTO.getPostId())) {
            throw new RuntimeException("User already liked this post");
        }

        User user = userRepository.findById(likeDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(likeDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        PostLike like = new PostLike(user, post);
        postLikeRepository.save(like);
    }

    public void unlikePost(Long userId, Long postId) {
        postLikeRepository.deleteLikeNative(userId, postId);
    }

    @Transactional(readOnly = true)
    public Long countLikes(Long postId) {
        return postLikeRepository.countLikesByPostId(postId);
    }

    @Transactional(readOnly = true)
    public List<UserActivityReportDTO> getTopActiveUsers() {
        return postLikeRepository.findTopActiveUsers();
    }
}
