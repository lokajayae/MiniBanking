package com.lokajayae.fraudservice.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FraudAlertResponse {
    private String id;
    private String transactionId;
    private String accountNumber;
    private BigDecimal totalAmount;
    private String reason;
    private List<TransactionResponse> relatedTransactions;
    private LocalDateTime detectedAt;
}