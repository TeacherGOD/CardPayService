package com.example.paymentprocessor.service;

import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.FinalPaymentStatus;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.event.TransactionProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final ApplicationEventPublisher eventPublisher;


    public FinalTransactionStatus processBankResponse(BankResponse bankResponse) {

        var transaction = new FinalTransactionStatus(
                bankResponse.bankTransactionId(),
                FinalPaymentStatus.fromSource(bankResponse.status()),
                bankResponse.reason()
        );

        log.info("Transaction processed: {}", transaction);

        eventPublisher.publishEvent(new TransactionProcessedEvent(this, transaction));

        return transaction;
    }
}
