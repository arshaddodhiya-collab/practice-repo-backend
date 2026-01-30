# Spring Boot Global Exception Handling Guide

This document explains the exception handling mechanism implemented in this project.

## Overview

We use **Global Exception Handling** to ensure that whenever an error occurs (like "User not found"), the API returns a consistent, structured JSON response instead of a default HTML error page.

## Components

### 1. The Custom Exception
**File:** `ResourceNotFoundException.java`
- A simple runtime exception used to indicate that a requested database record does not exist.
- **Usage:** Thrown by the Service layer when an ID is not valid.

### 2. The Error Response DTO
**File:** `ErrorResponse.java`
- Defines the structure of the JSON response sent to the client.
- **Fields:**
  - `timestamp`: When the error occurred.
  - `status`: HTTP status code (e.g., 404).
  - `error`: Short error title (e.g., "Not Found").
  - `message`: Detailed message (e.g., "User not found with id: 99").
  - `path`: The API path that was requested.

### 3. The Global Handler
**File:** `GlobalExceptionHandler.java`
- Annotated with `@ControllerAdvice`. This tells Spring: *"Use this class to handle exceptions thrown by ANY controller in the application."*
- **Methods:**
  - `handleResourceNotFoundException`: Catches `ResourceNotFoundException` and returns a 404 Not Found response.
  - `handleGlobalException`: Catches all other generic `Exception` types and returns a 500 Internal Server Error.

## The Execution Flow

Here is what happens when you request a user ID that doesn't exist:

1.  **Client Request:**
    `GET /users/99`

2.  **Controller Layer (`UserController`):**
    Calls `userService.getUserById(99)`.

3.  **Service Layer (`UserService`):**
    Tries to find the user.
    ```java
    return userRepository.findById(99)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: 99"));
    ```
    *Result: Exception is thrown!*

4.  **Global Exception Handler:**
    Spring catches the exception and looks for a matching `@ExceptionHandler`.
    It finds `handleResourceNotFoundException` in `GlobalExceptionHandler`.

5.  **Response Creation:**
    The handler creates an `ErrorResponse` object with the error details.

6.  **Final Response:**
    The API returns:
    ```json
    {
      "timestamp": "2026-01-30T10:00:00",
      "status": 404,
      "error": "Not Found",
      "message": "User not found with id: 99",
      "path": "/users/99"
    }
    ```
