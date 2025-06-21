package com.example.LibraryManagementSystem.repository;

import com.example.LibraryManagementSystem.model.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    List<BorrowHistory> findByStudent_Id(Long studentId);
    List<BorrowHistory> findByFineIsNull();
    List<BorrowHistory> findByReturnDateIsNull();
} 