package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.dto.BookDTO;
import java.util.List;

public interface BookService {
    BookDTO addBook(BookDTO bookDTO);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    List<BookDTO> getAllBooks();
    BookDTO getBookById(Long id);
    List<BookDTO> searchBooks(String keyword);
} 