package com.example.LibraryManagementSystem.web;

import com.example.LibraryManagementSystem.dto.BookDTO;
import com.example.LibraryManagementSystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books/v1")
public class BookWebController {
    @Autowired
    private BookService bookService;

    @GetMapping("/view")
    public String showBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book-list";
    }

    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "book-edit";
    }

    @PostMapping("/add")
    public String addBook(BookDTO bookDTO) {
        bookService.addBook(bookDTO);
        return "redirect:/books/v1/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books/v1/view";
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "book-edit";
    }

    @PostMapping("/update")
    public String updateBook(BookDTO bookDTO) {
        bookService.updateBook(bookDTO.getId(), bookDTO);
        return "redirect:/books/v1/view";
    }

    /**
     * @param id
     * @param author
     * @param isbn
     * @param title
     * @param model
     * @return
     */
    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String id,
                              @RequestParam(required = false) String author,
                              @RequestParam(required = false) String isbn,
                              @RequestParam(required = false) String title,
                              Model model) {
        if (id != null && !id.isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(id));
        } else if (author != null && !author.isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(author));
        } else if (isbn != null && !isbn.isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(isbn));
        } else if (title != null && !title.isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(title));
        } else {
            model.addAttribute("books", bookService.getAllBooks());
        }
        return "book-list";
    }
}