package com.example.paymentprocessor.repository;

import com.example.paymentprocessor.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByBankTransactionId(String bankTransactionId);

    Optional<Transaction> findByBankTransactionId(String s);

    Long countByBankTransactionId(String s);
}
