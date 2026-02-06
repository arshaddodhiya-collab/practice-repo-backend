package com.test.practice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeDTO {
    private Long id;
    private Long userId;
    private Long postId;
}
