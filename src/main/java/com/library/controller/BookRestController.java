package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookRestController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Optional<Book> book = bookService.findBookById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.findBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findBooksByTitle(title));
    }
    
    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchBooksByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.findBooksByAuthor(author));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.findBooksByCategory(category));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.findAvailableBooks());
    }
    
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        // Check if ISBN already exists
        if (bookService.findBookByIsbn(book.getIsbn()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book book) {
        if (!bookService.findBookById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        book.setId(id);
        Book updatedBook = bookService.saveBook(book);
        return ResponseEntity.ok(updatedBook);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        if (!bookService.findBookById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
