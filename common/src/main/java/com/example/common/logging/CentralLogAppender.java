package com.example.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.example.common.dto.LogEntry;
import com.example.common.enums.LogLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Setter
public class CentralLogAppender extends AppenderBase<ILoggingEvent> {
    private String serviceName;
    private String loggingServiceUrl;
    private WebClient webClient;
    private BlockingQueue<ILoggingEvent> queue;
    private Thread senderThread;
    private volatile boolean running = true;

    @Override
    public void start() {
        this.webClient = WebClient.builder()
                .baseUrl(loggingServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.queue = new LinkedBlockingQueue<>(1000);

        this.senderThread = new Thread(this::sendLogs);
        this.senderThread.setDaemon(true);
        this.senderThread.start();

        super.start();
    }

    @Override
    public void stop() {
        this.running = false;
        if (senderThread != null) {
            senderThread.interrupt();
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!queue.offer(event)) {
            addError("Log queue is full, dropping log event: " + event.getMessage());
        }
    }

    private void sendLogs() {
        while (running) {
            try {
                ILoggingEvent event = queue.take();
                sendLogToService(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                addError("Failed to send log to central service", e);
            }
        }
    }

    private void sendLogToService(ILoggingEvent event) {
        try {
            LogEntry logEntry = new LogEntry(
                    LocalDateTime.now(),
                    mapToLogLevel(event.getLevel()),
                    event.getFormattedMessage(),
                    serviceName);

            webClient.post()
                    .uri("/logs")
                    .bodyValue(logEntry)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(null, error ->
                            addError("Failed to send log to central service", error));
        } catch (Exception e) {
            addError("Error preparing log for central service", e);
        }
    }

    private LogLevel mapToLogLevel(Level level) {
        return switch (level.levelInt) {
            case Level.DEBUG_INT -> LogLevel.DEBUG;
            case Level.INFO_INT -> LogLevel.INFO;
            case Level.WARN_INT -> LogLevel.WARN;
            case Level.ERROR_INT -> LogLevel.ERROR;
            case Level.TRACE_INT -> LogLevel.TRACE;
            default -> LogLevel.INFO;
        };
    }
}