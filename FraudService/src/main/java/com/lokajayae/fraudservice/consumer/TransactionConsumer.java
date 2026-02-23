package com.lokajayae.fraudservice.consumer;

import com.lokajayae.fraudservice.event.TransactionEvent;
import com.lokajayae.fraudservice.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {

    private final FraudDetectionService fraudDetectionService;

    @KafkaListener(
            topics = "transaction.created",
            groupId = "fraud-detection-group"
    )
    public void consume(TransactionEvent event) {
        log.info("Received transaction event: {}", event.getTransactionId());
        fraudDetectionService.analyze(event);
    }
}