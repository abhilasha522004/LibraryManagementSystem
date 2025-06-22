package com.example.LibraryManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        BookNotFoundException.class,
        StudentNotFoundException.class,
        BorrowHistoryNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFoundExceptions(RuntimeException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Not found", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler({
        DuplicateBookException.class,
        DuplicateStudentException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public Object handleDuplicateExceptions(RuntimeException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("Duplicate entry", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler({
        InvalidBookDataException.class,
        InvalidStudentDataException.class,
        InvalidBorrowRequestException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleValidationExceptions(RuntimeException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Validation error", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler({
        BookAlreadyBorrowedException.class,
        NoCopiesAvailableException.class,
        BookAlreadyReturnedException.class,
        BorrowLimitExceededException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBusinessRuleExceptions(RuntimeException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Business rule violation", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleUnauthorizedException(UnauthorizedActionException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("Unauthorized", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler({DatabaseException.class, ExternalServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleSystemExceptions(RuntimeException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("System error", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler(MissingRequestIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMissingRequestId(MissingRequestIdException ex, HttpServletRequest request, Model model) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("Missing header", ex.getMessage()));
        } else {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Page not found");
        return "error";
    }

    static class ApiError {
        public String error;
        public String message;
        public ApiError(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
} 