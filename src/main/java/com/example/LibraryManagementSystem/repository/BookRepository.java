package com.example.LibraryManagementSystem.repository;

import com.example.LibraryManagementSystem.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByIsbnContaining(String isbn);
    Optional<Book> findById(Long id);
} 