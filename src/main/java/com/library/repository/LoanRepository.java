package com.library.repository;

import com.library.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {
    List<Loan> findByUserId(String userId);
    List<Loan> findByBookId(String bookId);
    List<Loan> findByStatus(Loan.Status status);
    List<Loan> findByDueDateBeforeAndStatus(Date date, Loan.Status status);
    long countByStatus(Loan.Status status);
}
