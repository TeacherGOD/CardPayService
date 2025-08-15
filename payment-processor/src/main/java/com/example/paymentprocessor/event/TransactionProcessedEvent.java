package com.example.paymentprocessor.event;

import com.example.paymentprocessor.entity.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionProcessedEvent extends ApplicationEvent {
    private final transient  Transaction transaction;

    public TransactionProcessedEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
