package com.example.LibraryManagementSystem.exception;

public class MissingRequestIdException extends RuntimeException {
    public MissingRequestIdException(String message) {
        super(message);
    }
} 