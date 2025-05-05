package com.library.controller;

import com.library.model.Loan;
import com.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {
    
    @Autowired
    private LoanService loanService;
    
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.findAllLoans());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable String id) {
        Optional<Loan> loan = loanService.findLoanById(id);
        return loan.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(loanService.findLoansByUserId(userId));
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Loan>> getLoansByBookId(@PathVariable String bookId) {
        return ResponseEntity.ok(loanService.findLoansByBookId(bookId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Loan>> getLoansByStatus(@PathVariable Loan.Status status) {
        return ResponseEntity.ok(loanService.findLoansByStatus(status));
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.findOverdueLoans());
    }
    
    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestParam String userId, 
                                          @RequestParam String bookId,
                                          @RequestParam(defaultValue = "14") int loanDays) {
        try {
            Loan loan = loanService.createLoan(userId, bookId, loanDays);
            return ResponseEntity.status(HttpStatus.CREATED).body(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnLoan(@PathVariable String id) {
        try {
            Loan loan = loanService.returnLoan(id);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Loan> updateLoanStatus(@PathVariable String id, 
                                               @RequestParam Loan.Status status) {
        try {
            Loan loan = loanService.updateLoanStatus(id, status);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable String id) {
        if (!loanService.findLoanById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
