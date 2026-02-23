package com.lokajayae.fraudservice.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}