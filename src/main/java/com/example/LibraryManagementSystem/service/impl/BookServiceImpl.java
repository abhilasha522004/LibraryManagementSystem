package com.example.LibraryManagementSystem.service.impl;

import com.example.LibraryManagementSystem.dto.BookDTO;
import com.example.LibraryManagementSystem.model.Book;
import com.example.LibraryManagementSystem.repository.BookRepository;
import com.example.LibraryManagementSystem.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.LibraryManagementSystem.util.LoggerUtil;

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
        LoggerUtil.info("Adding book: " + bookDTO);
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            LoggerUtil.error("Duplicate ISBN detected: " + bookDTO.getIsbn());
            throw new com.example.LibraryManagementSystem.exception.DuplicateBookException("Book with this ISBN already exists");
        }
        Book book = modelMapper.map(bookDTO, Book.class);
        book.setTotalCopies(book.getCopiesAvailable());
        Book savedBook = bookRepository.save(book);
        LoggerUtil.info("Book saved: " + savedBook);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    
    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        LoggerUtil.info("Updating book with id: " + id + ", data: " + bookDTO);
        Book book;
        try {
            book = bookRepository.findById(id).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.BookNotFoundException("Book not found"));
        } catch (com.example.LibraryManagementSystem.exception.BookNotFoundException e) {
            LoggerUtil.error("Book not found for update, id: " + id);
            throw e;
        }
        modelMapper.map(bookDTO, book);
        Book updatedBook = bookRepository.save(book);
        LoggerUtil.info("Book updated: " + updatedBook);
        return modelMapper.map(updatedBook, BookDTO.class);
    }


    @Override
    public void deleteBook(Long id) {
        LoggerUtil.info("Deleting book with id: " + id);
        try {
            bookRepository.deleteById(id);
        } catch (Exception e) {
            LoggerUtil.error("Error deleting book with id: " + id + ", error: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public List<BookDTO> getAllBooks() {
        LoggerUtil.info("Fetching all books");
        List<BookDTO> books = bookRepository.findAll().stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
        LoggerUtil.debug("Total books fetched: " + books.size());
        return books;
    }

    @Override
    public BookDTO getBookById(Long id) {
        LoggerUtil.info("Fetching book with id: " + id);
        Book book;
        try {
            book = bookRepository.findById(id).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.BookNotFoundException("Book not found"));
        } catch (com.example.LibraryManagementSystem.exception.BookNotFoundException e) {
            LoggerUtil.error("Book not found for id: " + id);
            throw e;
        }
        return modelMapper.map(book, BookDTO.class);
    }

    
    @Override
    public List<BookDTO> searchBooks(String keyword) {
        LoggerUtil.info("Searching books with keyword: " + keyword);
        Set<Book> results = new HashSet<>();
        try {
            Long id = Long.parseLong(keyword);
            bookRepository.findById(id).ifPresent(results::add);
        } catch (NumberFormatException ignored) {
            LoggerUtil.debug("Keyword is not a valid id: " + keyword);
        }
        results.addAll(bookRepository.findByAuthorContaining(keyword));
        results.addAll(bookRepository.findByIsbnContaining(keyword));
        results.addAll(bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword));
        LoggerUtil.info("Found " + results.size() + " books for keyword '" + keyword + "'");
        return results.stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
    }
} 