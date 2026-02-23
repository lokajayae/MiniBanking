package com.lokajayae.transactionservice.producer;

import com.lokajayae.transactionservice.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {

    private static final String TOPIC = "transaction.created";
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void publishTransaction(TransactionEvent event) {
        kafkaTemplate.send(TOPIC, event.getTransactionId(), event);
        log.info("Published transaction event: {}", event.getTransactionId());
    }
}