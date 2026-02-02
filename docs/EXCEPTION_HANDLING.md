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
- Now implemented as a **Java Record** for immutability and concise syntax.
- Defines the structure of the JSON response sent to the client.
- **Fields:**
  - `timestamp`: When the error occurred.
  - `status`: HTTP status code (e.g., 404).
  - `error`: **ErrorType Enum** (e.g., `NOT_FOUND`, `VALIDATION_ERROR`) for type safety.
  - `message`: Detailed message (String) or Map of validation errors.
  - `path`: The API path that was requested.

### 3. The Error Type Enum
**File:** `ErrorType.java`
- An `enum` that classifies errors into strict categories:
  - `VALIDATION_ERROR`
  - `AUTHENTICATION_ERROR`
  - `NOT_FOUND`
  - `INTERNAL_ERROR`

### 4. The Global Handler
**File:** `GlobalExceptionHandler.java`
- Annotated with `@ControllerAdvice`. This tells Spring: *"Use this class to handle exceptions thrown by ANY controller in the application."*
- **Methods:**
  - `handleResourceNotFoundException`: Catches `ResourceNotFoundException`, sets `ErrorType.NOT_FOUND`.
  - `handleValidationException`: Catches `MethodArgumentNotValidException`, sets `ErrorType.VALIDATION_ERROR`.
  - `handleGlobalException`: Catches all other generic `Exception` types, sets `ErrorType.INTERNAL_ERROR`.

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
    The handler creates an `ErrorResponse` record with `ErrorType.NOT_FOUND`.

6.  **Final Response:**
    The API returns:
    ```json
    {
      "timestamp": "2026-01-30T10:00:00",
      "status": 404,
      "error": "NOT_FOUND",
      "message": "User not found with id: 99",
      "path": "/users/99"
    }
    ```
