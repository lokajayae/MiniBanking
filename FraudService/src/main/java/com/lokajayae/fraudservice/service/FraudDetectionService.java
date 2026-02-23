package com.lokajayae.fraudservice.service;

import com.lokajayae.fraudservice.event.TransactionEvent;
import com.lokajayae.fraudservice.model.FraudAlert;
import com.lokajayae.fraudservice.repository.FraudAlertRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private static final BigDecimal LARGE_AMOUNT_THRESHOLD = new BigDecimal("10000000");
    private static final int MAX_TRANSACTIONS_PER_5_MIN = 3;

    private final FraudAlertRepository fraudAlertRepository;
    private final Map<String, List<TransactionRecord>> recentTransactions
            = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    private static class TransactionRecord {
        private String transactionId;
        private LocalDateTime timestamp;
    }

    public void analyze(TransactionEvent event) {
        checkLargeAmount(event);
        checkFrequentTransactions(event);
    }

    private void checkLargeAmount(TransactionEvent event) {
        if (event.getAmount().compareTo(LARGE_AMOUNT_THRESHOLD) <= 0) return;

        String accountNumber = event.getFromAccountNumber() != null
                ? event.getFromAccountNumber()
                : event.getToAccountNumber();

        saveAlert(
                event.getTransactionId(),
                accountNumber,
                event.getAmount(),
                "Transaction amount exceeds threshold of 10,000,000",
                List.of(event.getTransactionId())
        );
    }

    private void checkFrequentTransactions(TransactionEvent event) {
        if (event.getFromAccountNumber() == null) return;

        String accountNumber = event.getFromAccountNumber();
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        recentTransactions.putIfAbsent(accountNumber, new ArrayList<>());
        List<TransactionRecord> records = recentTransactions.get(accountNumber);

        List<TransactionRecord> recentOnly = records.stream()
                .filter(r -> r.getTimestamp().isAfter(fiveMinutesAgo))
                .collect(Collectors.toList());

        recentOnly.add(new TransactionRecord(event.getTransactionId(), LocalDateTime.now()));
        recentTransactions.put(accountNumber, recentOnly);

        // Only start acting from 4th transaction onwards
        if (recentOnly.size() < MAX_TRANSACTIONS_PER_5_MIN + 1) return;

        List<String> relatedIds = recentOnly.stream()
                .map(TransactionRecord::getTransactionId)
                .collect(Collectors.toList());

        // Check if existing alert already exists for this burst
        FraudAlert existingAlert = fraudAlertRepository
                .findLatestAlertByAccount(accountNumber, fiveMinutesAgo);

        if (existingAlert != null) {
                // UPDATE existing alert with new transaction ids
                existingAlert.setRelatedTransactionIds(String.join(",", relatedIds));
                existingAlert.setTransactionId(event.getTransactionId());
                fraudAlertRepository.save(existingAlert);
                log.warn("🚨 FRAUD UPDATED - Account: {}, Total transactions: {}",
                        accountNumber, relatedIds.size());
        } else {
                // CREATE new alert
                saveAlert(
                        event.getTransactionId(),
                        accountNumber,
                        event.getAmount(),
                        "More than " + MAX_TRANSACTIONS_PER_5_MIN + " transactions in 5 minutes",
                        relatedIds
                );
        }
        }

    private void saveAlert(String transactionId, String accountNumber,
                           BigDecimal amount, String reason, List<String> relatedIds) {
        String relatedTransactionIds = String.join(",", relatedIds);

        FraudAlert alert = FraudAlert.builder()
                .transactionId(transactionId)
                .accountNumber(accountNumber)
                .amount(amount)
                .reason(reason)
                .relatedTransactionIds(relatedTransactionIds)
                .build();

        fraudAlertRepository.save(alert);
        log.warn("🚨 FRAUD DETECTED - Transaction: {}, Account: {}, Reason: {}",
                transactionId, accountNumber, reason);
    }
}