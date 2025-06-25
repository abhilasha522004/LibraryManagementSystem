package com.example.LibraryManagementSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.dto.BookDTO;
import com.example.LibraryManagementSystem.service.BookService;
import com.example.LibraryManagementSystem.util.LoggerUtil;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping
    public BookDTO addBook(@RequestBody BookDTO bookDTO) {
        LoggerUtil.info("Adding a new book: " + bookDTO);
        return bookService.addBook(bookDTO);
    }

    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        LoggerUtil.info("Updating book with id: " + id + ", data: " + bookDTO);
        return bookService.updateBook(id, bookDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        LoggerUtil.info("Deleting book with id: " + id);
        bookService.deleteBook(id);
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        LoggerUtil.info("Fetching all books");
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        LoggerUtil.info("Fetching book with id: " + id);
        return bookService.getBookById(id);
    }

    @GetMapping("/search")
    public List<BookDTO> searchBooks(@RequestParam(required = false) String id,
                                     @RequestParam(required = false) String author,
                                     @RequestParam(required = false) String isbn,
                                     @RequestParam(required = false) String title) {
        LoggerUtil.info("Searching books with params - id: " + id + ", author: " + author + ", isbn: " + isbn + ", title: " + title);
        if (id != null && !id.isEmpty()) {
            return bookService.searchBooks(id);
        } else if (author != null && !author.isEmpty()) {
            return bookService.searchBooks(author);
        } else if (isbn != null && !isbn.isEmpty()) {
            return bookService.searchBooks(isbn);
        } else if (title != null && !title.isEmpty()) {
            return bookService.searchBooks(title);
        } else {
            return bookService.getAllBooks();
        }
    }
}