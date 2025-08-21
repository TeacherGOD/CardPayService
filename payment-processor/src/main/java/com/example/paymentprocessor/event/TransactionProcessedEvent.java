package com.example.paymentprocessor.event;

import com.example.paymentprocessor.dto.FinalTransactionStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionProcessedEvent extends ApplicationEvent {
    private final transient FinalTransactionStatus transaction;

    public TransactionProcessedEvent(Object source, FinalTransactionStatus transaction) {
        super(source);
        this.transaction = transaction;
    }
}
