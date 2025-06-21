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
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.getCopiesAvailable() <= 0) {
            throw new RuntimeException("No copies available");
        }
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepository.save(book);
        BorrowHistory borrowHistory = new BorrowHistory();
        borrowHistory.setStudent(student);
        borrowHistory.setBook(book);
        borrowHistory.setBorrowDate(LocalDate.now());
        borrowHistory.setDueDate(LocalDate.now().plusDays(30));
        borrowHistory.setReturnDate(null);
        return modelMapper.map(borrowHistoryRepository.save(borrowHistory), BorrowHistoryDTO.class);
    }

    @Override
    public BorrowHistoryDTO returnBook(Long borrowHistoryId) {
        BorrowHistory borrowHistory = borrowHistoryRepository.findById(borrowHistoryId).orElseThrow(() -> new RuntimeException("Borrow history not found"));
        if (borrowHistory.getReturnDate() != null) {
            throw new RuntimeException("Book already returned");
        }
        borrowHistory.setReturnDate(LocalDate.now());
        Book book = borrowHistory.getBook();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);
        return modelMapper.map(borrowHistoryRepository.save(borrowHistory), BorrowHistoryDTO.class);
    }

    @Override
    public List<BorrowHistoryDTO> getBorrowHistoryByStudent(Long studentId) {
        return borrowHistoryRepository.findByStudent_Id(studentId)
            .stream()
            .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<BorrowHistoryDTO> getAllBorrowHistory() {
        return borrowHistoryRepository.findAll().stream().map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<BorrowHistoryDTO> findByStudentId(Long studentId) {
        return borrowHistoryRepository.findByStudent_Id(studentId)
            .stream()
            .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<BorrowHistoryDTO> findByFineIsNull() {
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
        
        return borrowHistoryRepository.findByFineIsNull()
            .stream()
            .map(bh -> modelMapper.map(bh, BorrowHistoryDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<BorrowHistoryDTO> getOverdueWithNullFine() {
        return borrowHistoryRepository.findAll().stream()
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
    }

    @Override
    public void deleteBorrowHistory(Long id) {
        borrowHistoryRepository.deleteById(id);
    }
} 