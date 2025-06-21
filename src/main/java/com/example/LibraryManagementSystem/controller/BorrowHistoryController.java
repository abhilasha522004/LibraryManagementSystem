package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.dto.BorrowHistoryDTO;
import com.example.LibraryManagementSystem.service.BorrowHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/borrow-history")
public class BorrowHistoryController {
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    @GetMapping("/student/{studentId}")
    public List<BorrowHistoryDTO> getBorrowHistoryByStudent(@PathVariable Long studentId) {
        return borrowHistoryService.findByStudentId(studentId);
    }

    @GetMapping("/fine-null")
    public List<BorrowHistoryDTO> getBorrowHistoryWithNullFine() {
        return borrowHistoryService.findByFineIsNull();
    }
}
