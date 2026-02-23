package com.lokajayae.fraudservice.service;

import com.lokajayae.fraudservice.client.TransactionClient;
import com.lokajayae.fraudservice.dto.response.FraudAlertResponse;
import com.lokajayae.fraudservice.dto.response.TransactionResponse;
import com.lokajayae.fraudservice.model.FraudAlert;
import com.lokajayae.fraudservice.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionClient transactionClient;

    public List<FraudAlertResponse> getAllAlerts() {
        return fraudAlertRepository.findAllOrderByDetectedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FraudAlertResponse> getAlertsByAccount(String accountNumber) {
        return fraudAlertRepository.findByAccountNumber(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FraudAlertResponse mapToResponse(FraudAlert alert) {
        List<TransactionResponse> relatedTransactions = alert.getRelatedTransactionIds() != null
                ? Arrays.stream(alert.getRelatedTransactionIds().split(","))
                .map(transactionClient::getTransaction)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                : List.of();

        BigDecimal totalAmount = relatedTransactions.stream()
                .map(TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FraudAlertResponse.builder()
                .id(alert.getId())
                .transactionId(alert.getTransactionId())
                .accountNumber(alert.getAccountNumber())
                .totalAmount(totalAmount)
                .reason(alert.getReason())
                .relatedTransactions(relatedTransactions)
                .detectedAt(alert.getDetectedAt())
                .build();
    }
}