package com.example.LibraryManagementSystem.exception;

public class InvalidBorrowRequestException extends RuntimeException {
    public InvalidBorrowRequestException(String message) {
        super(message);
    }
} 