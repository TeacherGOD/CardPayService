package com.example.paymentprocessor.listener;


import com.example.paymentprocessor.entity.Transaction;
import com.example.paymentprocessor.event.TransactionProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JournalPreparationListener {

    @EventListener
    public void handleTransactionProcessed(TransactionProcessedEvent event) {
        Transaction transaction = event.getTransaction();
        log.debug("Preparing data for journal: {}", transaction);
    }
}
