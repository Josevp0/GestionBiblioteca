package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/guest")
public class GuestController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public String home(Model model) {
        // Add some statistics for the welcome page
        model.addAttribute("totalBooks", bookService.countBooks());
        model.addAttribute("availableBooks", bookService.countAvailableBooks());
        return "guest/home";
    }
    
    @GetMapping("/catalog")
    public String catalog(Model model) {
        model.addAttribute("books", bookService.findAllBooks());
        return "guest/catalog";
    }
    
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable String id, Model model) {
        Optional<Book> book = bookService.findBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "guest/book-detail";
        }
        return "redirect:/guest/catalog";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {
        // Search by title or author
        model.addAttribute("books", bookService.findBooksByTitle(query));
        model.addAttribute("searchQuery", query);
        return "guest/catalog";
    }
}
