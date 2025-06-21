package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.dto.BorrowHistoryDTO;
import java.util.List;

public interface BorrowHistoryService {
    BorrowHistoryDTO borrowBook(Long studentId, Long bookId);
    BorrowHistoryDTO returnBook(Long borrowHistoryId);
    List<BorrowHistoryDTO> getBorrowHistoryByStudent(Long studentId);
    List<BorrowHistoryDTO> getAllBorrowHistory();
    List<BorrowHistoryDTO> findByStudentId(Long studentId);
    List<BorrowHistoryDTO> findByFineIsNull();
    List<BorrowHistoryDTO> getOverdueWithNullFine();
    void deleteBorrowHistory(Long id);
} 