package com.example.paymentprocessor.service;

import com.example.common.dto.TransactionRecordRequest;
import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.FinalPaymentStatus;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.dto.NotificationRequestDto;
import com.example.paymentprocessor.event.TransactionProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final ApplicationEventPublisher eventPublisher;
    private final TransactionRecorderClient transactionRecorderClient;
    private final NotificationClient notificationClient;


    public FinalTransactionStatus processBankResponse(BankResponse bankResponse) {

        var transaction = new FinalTransactionStatus(
                bankResponse.bankTransactionId(),
                FinalPaymentStatus.fromSource(bankResponse.status()),
                bankResponse.reason()
        );

        TransactionRecordRequest recordDto = new TransactionRecordRequest (
                bankResponse.amount(),
                bankResponse.currency(),
                transaction.status(),
                bankResponse.bankTransactionId(),
                bankResponse.reason(),
                bankResponse.merchantId()
        );

        transactionRecorderClient.recordTransaction(recordDto)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    log.info("Transaction saved successfully");
                    NotificationRequestDto notificationRequest = new NotificationRequestDto(
                            bankResponse.email(),
                            bankResponse.bankTransactionId(),
                            transaction.status());

                    notificationClient.sendNotification(notificationRequest)
                            .doOnError(e -> log.error("Failed to send notification", e))
                            .subscribe();
                })
                .onErrorResume(e -> Mono.empty())
                .subscribe();

        log.info("Transaction processed: {}", transaction);

        eventPublisher.publishEvent(new TransactionProcessedEvent(this, transaction));

        return transaction;
    }
}
