package com.example.LibraryManagementSystem.service.impl;

import com.example.LibraryManagementSystem.dto.BookDTO;
import com.example.LibraryManagementSystem.model.Book;
import com.example.LibraryManagementSystem.repository.BookRepository;
import com.example.LibraryManagementSystem.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new RuntimeException("Book with this ISBN already exists");
        }
        Book book = modelMapper.map(bookDTO, Book.class);
        book.setTotalCopies(book.getCopiesAvailable());
        return modelMapper.map(bookRepository.save(book), BookDTO.class);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        modelMapper.map(bookDTO, book);
        return modelMapper.map(bookRepository.save(book), BookDTO.class);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long id) {
        return modelMapper.map(bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found")), BookDTO.class);
    }

    @Override
    public List<BookDTO> searchBooks(String keyword) {
        Set<Book> results = new HashSet<>();
        // Search by ID if keyword is a number
        try {
            Long id = Long.parseLong(keyword);
            bookRepository.findById(id).ifPresent(results::add);
        } catch (NumberFormatException ignored) {}
        // Search by author
        results.addAll(bookRepository.findByAuthorContaining(keyword));
        // Search by ISBN
        results.addAll(bookRepository.findByIsbnContaining(keyword));
        // Also search by title or author (existing logic)
        results.addAll(bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword));
        return results.stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
    }
} 