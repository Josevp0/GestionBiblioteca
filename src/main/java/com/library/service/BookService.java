package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> findBookById(String id) {
        return bookRepository.findById(id);
    }
    
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    public List<Book> findBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    public List<Book> findAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }
    
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
    
    public boolean decreaseAvailableCopies(String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }
    
    public boolean increaseAvailableCopies(String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() < book.getTotalCopies()) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }
    
    public long countBooks() {
        return bookRepository.count();
    }
    
    public long countAvailableBooks() {
        return bookRepository.countByAvailableCopiesGreaterThan(0);
    }
}
