package com.example.transactionrecording.repository;

import com.example.transactionrecording.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRecordRepository extends JpaRepository<Transaction, Long> {
    boolean existsByBankTransactionId(String s);

    Optional<Transaction> findByBankTransactionId(String transactionId);
}
