package com.example.LibraryManagementSystem.web;

import com.example.LibraryManagementSystem.dto.BorrowHistoryDTO;
import com.example.LibraryManagementSystem.service.BorrowHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/borrow-history/v1")
public class BorrowHistoryWebController {
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    @GetMapping("/view")
    public String showBorrowHistory(@RequestParam(required = false) String filter, Model model) {
        if (filter != null && filter.equals("overdue")) {
            model.addAttribute("borrowHistory", borrowHistoryService.getOverdueWithNullFine());
        } else {
            model.addAttribute("borrowHistory", borrowHistoryService.getAllBorrowHistory());
        }
        return "borrow-history-list";
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam Long studentId, @RequestParam Long bookId) {
        borrowHistoryService.borrowBook(studentId, bookId);
        return "redirect:/borrow-history/v1/view";
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam Long borrowHistoryId) {
        borrowHistoryService.returnBook(borrowHistoryId);
        return "redirect:/borrow-history/v1/view";
    }

    @PostMapping("/delete/{id}")
    public String deleteBorrowHistory(@PathVariable Long id) {
        borrowHistoryService.deleteBorrowHistory(id);
        return "redirect:/borrow-history/v1/view";
    }
} 