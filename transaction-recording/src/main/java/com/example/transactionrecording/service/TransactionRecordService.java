package com.example.transactionrecording.service;

import com.example.common.dto.TransactionRecordRequest;
import com.example.common.dto.TransactionRecordResponse;
import com.example.transactionrecording.entity.Transaction;
import com.example.transactionrecording.exception.DuplicateTransactionException;
import com.example.transactionrecording.exception.TransactionNotFoundException;
import com.example.transactionrecording.repository.TransactionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionRecordService {

    private final TransactionRecordRepository repository;

    public TransactionRecordResponse saveTransaction(TransactionRecordRequest dto) {
        if (repository.existsByBankTransactionId(dto.bankTransactionId())) {
            log.info("Bank transaction ({}) already exists", dto.bankTransactionId());
            throw new DuplicateTransactionException(
                    "Duplicate transaction: " + dto.bankTransactionId()
            );
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.amount());
        transaction.setCurrency(dto.currency());
        transaction.setStatus(dto.status());
        transaction.setBankTransactionId(dto.bankTransactionId());
        transaction.setReason(dto.reason());
        transaction.setMerchantId(dto.merchantId());

        transaction=repository.save(transaction);
        log.info("Saving transaction: {}", dto.bankTransactionId());
        return convertToDto(transaction);
    }

    public TransactionRecordResponse getTransaction(String bankTransactionId) {
        log.debug("Getting transaction by bankTransactionId: {}", bankTransactionId);
        return repository.findByBankTransactionId(bankTransactionId)
                .map(this::convertToDto)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with bankTransactionId: " + bankTransactionId));
    }

    private TransactionRecordResponse  convertToDto(Transaction entity) {
        return new TransactionRecordResponse(
                entity.getId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getBankTransactionId(),
                entity.getReason(),
                entity.getMerchantId());
    }
}
