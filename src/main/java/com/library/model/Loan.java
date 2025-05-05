package com.library.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "loans")
public class Loan {
    @Id
    private String id;
    private String userId;
    private String bookId;
    private Date loanDate;
    private Date dueDate;
    private Date returnDate;
    private Status status;
    private String notes;

    public enum Status {
        ACTIVE, RETURNED, OVERDUE, LOST
    }

    // Constructors
    public Loan() {
        this.loanDate = new Date();
        this.status = Status.ACTIVE;
    }

    public Loan(String userId, String bookId, Date dueDate) {
        this();
        this.userId = userId;
        this.bookId = bookId;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                '}';
    }
}
