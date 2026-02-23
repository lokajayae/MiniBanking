package com.lokajayae.transactionservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {

    private String fromAccountNumber;
    private String toAccountNumber;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description;
}