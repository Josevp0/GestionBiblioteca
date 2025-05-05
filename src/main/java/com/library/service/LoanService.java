package com.library.service;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private BookService bookService;
    
    public List<Loan> findAllLoans() {
        return loanRepository.findAll();
    }
    
    public Optional<Loan> findLoanById(String id) {
        return loanRepository.findById(id);
    }
    
    public List<Loan> findLoansByUserId(String userId) {
        return loanRepository.findByUserId(userId);
    }
    
    public List<Loan> findLoansByBookId(String bookId) {
        return loanRepository.findByBookId(bookId);
    }
    
    public List<Loan> findLoansByStatus(Loan.Status status) {
        return loanRepository.findByStatus(status);
    }
    
    public List<Loan> findOverdueLoans() {
        return loanRepository.findByDueDateBeforeAndStatus(new Date(), Loan.Status.ACTIVE);
    }
    
    public Loan createLoan(String userId, String bookId, int loanDays) {
        // Check if book is available
        if (!bookService.decreaseAvailableCopies(bookId)) {
            throw new RuntimeException("Book is not available for loan");
        }
        
        // Create new loan
        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setLoanDate(new Date());
        
        // Calculate due date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loan.getLoanDate());
        calendar.add(Calendar.DAY_OF_MONTH, loanDays);
        loan.setDueDate(calendar.getTime());
        
        return loanRepository.save(loan);
    }
    
    public Loan returnLoan(String loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            if (loan.getStatus() == Loan.Status.ACTIVE) {
                loan.setStatus(Loan.Status.RETURNED);
                loan.setReturnDate(new Date());
                bookService.increaseAvailableCopies(loan.getBookId());
                return loanRepository.save(loan);
            }
        }
        throw new RuntimeException("Loan not found or already returned");
    }
    
    public Loan updateLoanStatus(String loanId, Loan.Status status) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            loan.setStatus(status);
            
            // If marking as returned, update return date and increase available copies
            if (status == Loan.Status.RETURNED && loan.getReturnDate() == null) {
                loan.setReturnDate(new Date());
                bookService.increaseAvailableCopies(loan.getBookId());
            }
            
            return loanRepository.save(loan);
        }
        throw new RuntimeException("Loan not found");
    }
    
    public Loan updateLoan(Loan loan) {
        return loanRepository.save(loan);
    }
    
    public void deleteLoan(String id) {
        loanRepository.deleteById(id);
    }
    
    public long countLoans() {
        return loanRepository.count();
    }
    
    public long countActiveLoans() {
        return loanRepository.countByStatus(Loan.Status.ACTIVE);
    }
    
    public long countOverdueLoans() {
        return findOverdueLoans().size();
    }
}
