package com.library.model;

public class Statistics {
    private long totalUsers;
    private long totalBooks;
    private long totalLoans;
    private long activeLoans;
    private long overdueLoans;
    private long availableBooks;

    // Constructors
    public Statistics() {
    }

    public Statistics(long totalUsers, long totalBooks, long totalLoans, 
                     long activeLoans, long overdueLoans, long availableBooks) {
        this.totalUsers = totalUsers;
        this.totalBooks = totalBooks;
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
        this.overdueLoans = overdueLoans;
        this.availableBooks = availableBooks;
    }

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(long totalLoans) {
        this.totalLoans = totalLoans;
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(long activeLoans) {
        this.activeLoans = activeLoans;
    }

    public long getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(long overdueLoans) {
        this.overdueLoans = overdueLoans;
    }

    public long getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(long availableBooks) {
        this.availableBooks = availableBooks;
    }
}
