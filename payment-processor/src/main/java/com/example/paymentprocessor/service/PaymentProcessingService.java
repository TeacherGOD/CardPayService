package com.example.paymentprocessor.service;

import com.example.common.dto.bank.BankResponse;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.entity.Transaction;
import com.example.paymentprocessor.event.TransactionProcessedEvent;
import com.example.paymentprocessor.exception.DuplicateTransactionException;
import com.example.paymentprocessor.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public FinalTransactionStatus processBankResponse(BankResponse bankResponse) {
        if (transactionRepository.existsByBankTransactionId(bankResponse.bankTransactionId())) {
            throw new DuplicateTransactionException(
                    "Duplicate transaction: " + bankResponse.bankTransactionId()
            );
        }

        Transaction transaction = new Transaction();
        transaction.setBankTransactionId(bankResponse.bankTransactionId());
        transaction.setAmount(bankResponse.amount());
        transaction.setCurrency(bankResponse.currency());
        transaction.setMerchantId(bankResponse.merchantId());
        transaction.setStatus(bankResponse.status());
        transaction.setReason(bankResponse.reason());

        transactionRepository.save(transaction);

        log.info("Transaction processed: {}", transaction);

        eventPublisher.publishEvent(new TransactionProcessedEvent(this, transaction));

        return new FinalTransactionStatus(
                bankResponse.bankTransactionId(),
                bankResponse.status(),
                bankResponse.reason()
        );
    }
}
