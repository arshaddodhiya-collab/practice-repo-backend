package com.test.practice.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Object message;   // can be String OR Map<String, String>
    private String path;

    public ErrorResponse(LocalDateTime timestamp,
                         int status,
                         String error,
                         Object message,
                         String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Object getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
