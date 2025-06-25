package com.example.LibraryManagementSystem.service.impl;

import com.example.LibraryManagementSystem.dto.BorrowHistoryDTO;
import com.example.LibraryManagementSystem.model.BorrowHistory;
import com.example.LibraryManagementSystem.model.Student;
import com.example.LibraryManagementSystem.model.Book;
import com.example.LibraryManagementSystem.repository.BorrowHistoryRepository;
import com.example.LibraryManagementSystem.repository.StudentRepository;
import com.example.LibraryManagementSystem.repository.BookRepository;
import com.example.LibraryManagementSystem.service.BorrowHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import com.example.LibraryManagementSystem.util.LoggerUtil;

@Service
public class BorrowHistoryServiceImpl implements BorrowHistoryService {
    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BorrowHistoryDTO borrowBook(Long studentId, Long bookId) {
        LoggerUtil.info("Borrowing book. studentId: " + studentId + ", bookId: " + bookId);
        Student student;
        Book book;
        try {
            student = studentRepository.findById(studentId).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.StudentNotFoundException("Student not found"));
        } catch (com.example.LibraryManagementSystem.exception.StudentNotFoundException e) {
            LoggerUtil.error("Student not found for borrow, id: " + studentId);
            throw e;
        }
        try {
            book = bookRepository.findById(bookId).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.BookNotFoundException("Book not found"));
        } catch (com.example.LibraryManagementSystem.exception.BookNotFoundException e) {
            LoggerUtil.error("Book not found for borrow, id: " + bookId);
            throw e;
        }
        if (book.getCopiesAvailable() <= 0) {
            LoggerUtil.error("No copies available for bookId: " + bookId);
            throw new com.example.LibraryManagementSystem.exception.NoCopiesAvailableException("No copies available");
        }
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepository.save(book);
        BorrowHistory borrowHistory = new BorrowHistory();
        borrowHistory.setStudent(student);
        borrowHistory.setBook(book);
        borrowHistory.setBorrowDate(LocalDate.now());
        borrowHistory.setDueDate(LocalDate.now().plusDays(30));
        borrowHistory.setReturnDate(null);
        BorrowHistory saved = borrowHistoryRepository.save(borrowHistory);
        LoggerUtil.info("Book borrowed: " + saved);
        return modelMapper.map(saved, BorrowHistoryDTO.class);
    }

    @Override
    public BorrowHistoryDTO returnBook(Long borrowHistoryId) {
        LoggerUtil.info("Returning book. borrowHistoryId: " + borrowHistoryId);
        BorrowHistory borrowHistory;
        try {
            borrowHistory = borrowHistoryRepository.findById(borrowHistoryId).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.BorrowHistoryNotFoundException("Borrow history not found"));
        } catch (com.example.LibraryManagementSystem.exception.BorrowHistoryNotFoundException e) {
            LoggerUtil.error("Borrow history not found for return, id: " + borrowHistoryId);
            throw e;
        }
        if (borrowHistory.getReturnDate() != null) {
            LoggerUtil.error("Book already returned. borrowHistoryId: " + borrowHistoryId);
            throw new com.example.LibraryManagementSystem.exception.BookAlreadyReturnedException("Book already returned");
        }
        borrowHistory.setReturnDate(LocalDate.now());
        Book book = borrowHistory.getBook();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);
        BorrowHistory saved = borrowHistoryRepository.save(borrowHistory);
        LoggerUtil.info("Book returned: " + saved);
        return modelMapper.map(saved, BorrowHistoryDTO.class);
    }

    @Override
    public List<BorrowHistoryDTO> getBorrowHistoryByStudent(Long studentId) {
        LoggerUtil.info("Fetching borrow history for studentId: " + studentId);
        List<BorrowHistoryDTO> list = borrowHistoryRepository.findByStudent_Id(studentId)
            .stream()
            .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
            .collect(Collectors.toList());
        LoggerUtil.debug("Total borrow history records for studentId " + studentId + ": " + list.size());
        return list;
    }

    @Override
    public List<BorrowHistoryDTO> getAllBorrowHistory() {
        LoggerUtil.info("Fetching all borrow history");
        List<BorrowHistoryDTO> list = borrowHistoryRepository.findAll().stream().map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class)).collect(Collectors.toList());
        LoggerUtil.debug("Total borrow history records: " + list.size());
        return list;
    }

    @Override
    public List<BorrowHistoryDTO> findByStudentId(Long studentId) {
        LoggerUtil.info("Finding borrow history by studentId: " + studentId);
        List<BorrowHistoryDTO> list = borrowHistoryRepository.findByStudent_Id(studentId)
            .stream()
            .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
            .collect(Collectors.toList());
        LoggerUtil.debug("Found " + list.size() + " borrow history records for studentId '" + studentId + "'");
        return list;
    }

    @Override
    public List<BorrowHistoryDTO> findByFineIsNull() {
        LoggerUtil.info("Fetching borrow history with null fine");
        List<BorrowHistory> histories = borrowHistoryRepository.findByFineIsNull();
        for (BorrowHistory bh : histories) {
            if (bh.getReturnDate() != null) {
                long daysBetween = ChronoUnit.DAYS.between(bh.getBorrowDate().plusDays(30), bh.getReturnDate());
                if (daysBetween > 0) {
                    bh.setFine(daysBetween * 2.0);
                    borrowHistoryRepository.save(bh);
                }
            }
        }
        
        List<BorrowHistoryDTO> list = borrowHistoryRepository.findByFineIsNull()
            .stream()
            .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
            .collect(Collectors.toList());
        LoggerUtil.debug("Total borrow history records with null fine: " + list.size());
        return list;
    }

    @Override
    public List<BorrowHistoryDTO> getOverdueWithNullFine() {
        LoggerUtil.info("Fetching overdue borrow history with null fine");
        List<BorrowHistoryDTO> list = borrowHistoryRepository.findAll().stream()
                .filter(bh -> bh.getFine() == null)
                .filter(bh -> {
                    LocalDate dueDate = bh.getBorrowDate().plusDays(30);
                    if (bh.getReturnDate() == null) { 
                        return LocalDate.now().isAfter(dueDate);
                    } else { 
                        return bh.getReturnDate().isAfter(dueDate);
                    }
                })
                .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
                .collect(Collectors.toList());
        LoggerUtil.debug("Total overdue borrow history records with null fine: " + list.size());
        return list;
    }

    @Override
    public void deleteBorrowHistory(Long id) {
        LoggerUtil.info("Deleting borrow history with id: " + id);
        try {
            borrowHistoryRepository.deleteById(id);
        } catch (Exception e) {
            LoggerUtil.error("Error deleting borrow history with id: " + id + ", error: " + e.getMessage());
            throw e;
        }
    }
} 