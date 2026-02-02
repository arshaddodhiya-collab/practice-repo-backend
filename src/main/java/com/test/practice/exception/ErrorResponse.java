package com.test.practice.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        ErrorType error,
        Object message,
        String path) {
}
