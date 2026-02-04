package com.test.practice.dto;

public interface UserActivityReportDTO {
    Long getUserId(); // Expects a column named "userId" or "user_id"

    String getUserName(); // Expects a column named "userName" or "user_name"

    Long getActivityCount();// Expects a column named "activityCount" or "activity_count"
}