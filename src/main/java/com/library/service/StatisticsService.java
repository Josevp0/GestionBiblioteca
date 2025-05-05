package com.library.service;

import com.library.model.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private LoanService loanService;
    
    public Statistics getLibraryStatistics() {
        Statistics stats = new Statistics();
        
        stats.setTotalUsers(userService.countUsers());
        stats.setTotalBooks(bookService.countBooks());
        stats.setTotalLoans(loanService.countLoans());
        stats.setActiveLoans(loanService.countActiveLoans());
        stats.setOverdueLoans(loanService.countOverdueLoans());
        stats.setAvailableBooks(bookService.countAvailableBooks());
        
        return stats;
    }
}
