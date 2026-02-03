package com.test.practice.controller;

import com.test.practice.dto.UserActivityReportDTO;
import com.test.practice.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final PostLikeService postLikeService;

    public ReportController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @GetMapping("/active-users")
    public ResponseEntity<List<UserActivityReportDTO>> getTopActiveUsers() {
        return ResponseEntity.ok(postLikeService.getTopActiveUsers());
    }
}
