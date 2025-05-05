package com.library.controller;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Statistics;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.FileStorageService;
import com.library.service.LoanService;
import com.library.service.StatisticsService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping
    public String dashboard(Model model) {
        Statistics stats = statisticsService.getLibraryStatistics();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }
    
    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users/list";
    }
    
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "admin/users/form";
    }
    
    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable String id, Model model) {
        Optional<User> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            
            // Get user's loans
            List<Loan> loans = loanService.findLoansByUserId(id);
            model.addAttribute("loans", loans);
            
            // Get book details for each loan
            Map<String, Book> bookMap = new HashMap<>();
            for (Loan loan : loans) {
                Optional<Book> bookOpt = bookService.findBookById(loan.getBookId());
                bookOpt.ifPresent(book -> bookMap.put(loan.getBookId(), book));
            }
            model.addAttribute("bookMap", bookMap);
            
            return "admin/users/view";
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable String id, Model model) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("roles", User.Role.values());
            return "admin/users/edit";
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/delete/{id}")
    public String deleteUserForm(@PathVariable String id, Model model) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "admin/users/delete";
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user) {
        // Asegurarse de que estamos creando un nuevo usuario
        user.setId(null);
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user) {
        // Verificar que el usuario existe antes de actualizarlo
        Optional<User> existingUser = userService.findUserById(user.getId());
        if (!existingUser.isPresent()) {
            return "redirect:/admin/users";
        }
        
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam String id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    
    // Book Management
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAllBooks());
        return "admin/books/list";
    }
    
    @GetMapping("/books/view/{id}")
    public String viewBook(@PathVariable String id, Model model) {
        Optional<Book> bookOpt = bookService.findBookById(id);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            model.addAttribute("book", book);
            
            List<Loan> loans = loanService.findLoansByBookId(id);
            model.addAttribute("loans", loans);
            
            Map<String, User> userMap = new HashMap<>();
            for (Loan loan : loans) {
                Optional<User> userOpt = userService.findUserById(loan.getUserId());
                userOpt.ifPresent(user -> userMap.put(loan.getUserId(), user));
            }
            model.addAttribute("userMap", userMap);
            
            return "admin/books/view";
        }
        return "redirect:/admin/books";
    }
    
    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/books/form";
    }
    
    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable String id, Model model) {
        Optional<Book> book = bookService.findBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "admin/books/edit";
        }
        return "redirect:/admin/books";
    }
    
    @GetMapping("/books/delete/{id}")
    public String deleteBookForm(@PathVariable String id, Model model) {
        Optional<Book> book = bookService.findBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "admin/books/delete";
        }
        return "redirect:/admin/books";
    }
    
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book book, 
                         @RequestParam(required = false) String tagsString,
                         @RequestParam(required = false) MultipartFile imageFile,
                         @RequestParam(required = false) MultipartFile pdfFile) throws IOException {
        
        book.setId(null);
        
        if (tagsString != null && !tagsString.isEmpty()) {
            book.setTags(Arrays.asList(tagsString.split("\\s*,\\s*")));
        }
        
        // Procesar imagen
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileId = fileStorageService.storeFile(imageFile, "Portada del libro: " + book.getTitle());
            book.setImageFileId(imageFileId);
            book.setImageFileName(imageFile.getOriginalFilename());
        }
        
        // Procesar PDF
        if (pdfFile != null && !pdfFile.isEmpty()) {
            String pdfFileId = fileStorageService.storeFile(pdfFile, "PDF del libro: " + book.getTitle());
            book.setPdfFileId(pdfFileId);
            book.setPdfFileName(pdfFile.getOriginalFilename());
        }
        
        bookService.saveBook(book);
        return "redirect:/admin/books";
    }
    
    @PostMapping("/books/update")
    public String updateBook(@ModelAttribute Book book, 
                           @RequestParam(required = false) String tagsString,
                           @RequestParam(required = false) MultipartFile imageFile,
                           @RequestParam(required = false) MultipartFile pdfFile) throws IOException {
        
        Optional<Book> existingBook = bookService.findBookById(book.getId());
        if (!existingBook.isPresent()) {
            return "redirect:/admin/books";
        }
        
        if (tagsString != null && !tagsString.isEmpty()) {
            book.setTags(Arrays.asList(tagsString.split("\\s*,\\s*")));
        }
        
        Book currentBook = existingBook.get();
        
        // Procesar imagen
        if (imageFile != null && !imageFile.isEmpty()) {
            // Eliminar la imagen anterior si existe
            if (currentBook.getImageFileId() != null) {
                fileStorageService.deleteFile(currentBook.getImageFileId());
            }
            String imageFileId = fileStorageService.storeFile(imageFile, "Portada del libro: " + book.getTitle());
            book.setImageFileId(imageFileId);
            book.setImageFileName(imageFile.getOriginalFilename());
        } else {
            // Mantener la imagen existente si no se sube una nueva
            book.setImageFileId(currentBook.getImageFileId());
            book.setImageFileName(currentBook.getImageFileName());
        }
        
        // Procesar PDF
        if (pdfFile != null && !pdfFile.isEmpty()) {
            // Eliminar el PDF anterior si existe
            if (currentBook.getPdfFileId() != null) {
                fileStorageService.deleteFile(currentBook.getPdfFileId());
            }
            String pdfFileId = fileStorageService.storeFile(pdfFile, "PDF del libro: " + book.getTitle());
            book.setPdfFileId(pdfFileId);
            book.setPdfFileName(pdfFile.getOriginalFilename());
        } else {
            // Mantener el PDF existente si no se sube uno nuevo
            book.setPdfFileId(currentBook.getPdfFileId());
            book.setPdfFileName(currentBook.getPdfFileName());
        }
        
        bookService.saveBook(book);
        return "redirect:/admin/books";
    }
    
    @PostMapping("/books/delete")
    public String deleteBook(@RequestParam String id) {
        Optional<Book> bookOpt = bookService.findBookById(id);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            // Eliminar archivos asociados
            if (book.getImageFileId() != null) {
                fileStorageService.deleteFile(book.getImageFileId());
            }
            if (book.getPdfFileId() != null) {
                fileStorageService.deleteFile(book.getPdfFileId());
            }
        }
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }
    
    // Métodos para servir archivos
    @GetMapping("/books/image/{id}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String id) throws IOException {
        Book book = bookService.findBookById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        if (book.getImageFileId() == null) {
            throw new RuntimeException("El libro no tiene imagen");
        }
        
        InputStream inputStream = fileStorageService.getFile(book.getImageFileId());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // o MediaType.IMAGE_PNG según corresponda
                .body(new InputStreamResource(inputStream));
    }
    
    @GetMapping("/books/pdf/{id}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String id) throws IOException {
        Book book = bookService.findBookById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        if (book.getPdfFileId() == null) {
            throw new RuntimeException("El libro no tiene PDF");
        }
        
        InputStream inputStream = fileStorageService.getFile(book.getPdfFileId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + book.getPdfFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
    
    // Loan Management
    @GetMapping("/loans")
    public String listLoans(Model model) {
        model.addAttribute("loans", loanService.findAllLoans());
        return "admin/loans/list";
    }
    
    @GetMapping("/loans/view/{id}")
    public String viewLoan(@PathVariable String id, Model model) {
        Optional<Loan> loanOpt = loanService.findLoanById(id);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            model.addAttribute("loan", loan);
            
            // Get user details
            Optional<User> userOpt = userService.findUserById(loan.getUserId());
            userOpt.ifPresent(user -> model.addAttribute("user", user));
            
            // Get book details
            Optional<Book> bookOpt = bookService.findBookById(loan.getBookId());
            bookOpt.ifPresent(book -> model.addAttribute("book", book));
            
            return "admin/loans/view";
        }
        return "redirect:/admin/loans";
    }
    
    @GetMapping("/loans/new")
    public String newLoanForm(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("books", bookService.findAvailableBooks());
        return "admin/loans/form";
    }
    
    @GetMapping("/loans/edit/{id}")
    public String editLoanForm(@PathVariable String id, Model model) {
        Optional<Loan> loan = loanService.findLoanById(id);
        if (loan.isPresent()) {
            Loan loanObj = loan.get();
            
            // Get user and book details for display
            Optional<User> user = userService.findUserById(loanObj.getUserId());
            Optional<Book> book = bookService.findBookById(loanObj.getBookId());
            
            model.addAttribute("loan", loanObj);
            model.addAttribute("userName", user.isPresent() ? user.get().getFullName() : "Usuario Desconocido");
            model.addAttribute("bookTitle", book.isPresent() ? book.get().getTitle() : "Libro Desconocido");
            
            return "admin/loans/edit";
        }
        return "redirect:/admin/loans";
    }
    
    @GetMapping("/loans/delete/{id}")
    public String deleteLoanForm(@PathVariable String id, Model model) {
        Optional<Loan> loan = loanService.findLoanById(id);
        if (loan.isPresent()) {
            model.addAttribute("loan", loan.get());
            return "admin/loans/delete";
        }
        return "redirect:/admin/loans";
    }
    
    @PostMapping("/loans/save")
    public String saveLoan(@RequestParam String userId, 
                          @RequestParam String bookId,
                          @RequestParam(defaultValue = "14") int loanDays) {
        try {
            loanService.createLoan(userId, bookId, loanDays);
        } catch (RuntimeException e) {
            // Handle error
        }
        return "redirect:/admin/loans";
    }
    
    @PostMapping("/loans/update")
    public String updateLoan(@ModelAttribute Loan loan) {
        Optional<Loan> existingLoan = loanService.findLoanById(loan.getId());
        if (existingLoan.isPresent()) {
            Loan originalLoan = existingLoan.get();
            
            // Update only specific fields
            originalLoan.setDueDate(loan.getDueDate());
            originalLoan.setStatus(loan.getStatus());
            originalLoan.setReturnDate(loan.getReturnDate());
            originalLoan.setNotes(loan.getNotes());
            
            // If status changed to RETURNED, handle book availability
            if (loan.getStatus() == Loan.Status.RETURNED && originalLoan.getStatus() != Loan.Status.RETURNED) {
                bookService.increaseAvailableCopies(originalLoan.getBookId());
            }
            
            loanService.updateLoan(originalLoan);
        }
        return "redirect:/admin/loans";
    }
    
    @GetMapping("/loans/return/{id}")
    public String returnLoan(@PathVariable String id) {
        try {
            loanService.returnLoan(id);
        } catch (RuntimeException e) {
            // Handle error
        }
        return "redirect:/admin/loans";
    }
    
    @PostMapping("/loans/delete")
    public String deleteLoan(@RequestParam String id) {
        loanService.deleteLoan(id);
        return "redirect:/admin/loans";
    }
}
