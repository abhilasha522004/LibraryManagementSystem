package com.example.LibraryManagementSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String>getAllBooks(){
        return ResponseEntity.ok("Hello World");
    }
    
}
