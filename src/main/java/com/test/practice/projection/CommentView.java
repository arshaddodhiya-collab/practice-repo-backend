package com.test.practice.projection;

import java.time.LocalDateTime;

public interface CommentView {
    Long getId();

    String getText();

    LocalDateTime getCreatedAt();

    UserSummary getUser();

    PostSummary getPost();

    interface UserSummary {
        Long getId();

        String getName();
    }

    interface PostSummary {
        Long getId();
    }
}
