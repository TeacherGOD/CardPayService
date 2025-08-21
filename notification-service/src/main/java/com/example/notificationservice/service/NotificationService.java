package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotificationService {

    public NotificationResponse sendNotification(NotificationRequest request) {
        try {
            TimeUnit.MILLISECONDS.sleep(1000 + (long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Notification sent to email: {}, transaction: {}, status: {}",
                request.email(), request.transactionId(), request.status());

        return new NotificationResponse(
        true,
        "Notification sent successfully");
    }
}