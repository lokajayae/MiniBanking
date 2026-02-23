package com.lokajayae.transactionservice.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountResponse {
    private String id;
    private String name;
    private String accountNumber;
    private BigDecimal balance;
}