package com.lokajayae.transactionservice.client;

import com.lokajayae.transactionservice.dto.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AccountClient {

    private final RestTemplate restTemplate;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    public AccountResponse getAccount(String accountNumber) {
        return restTemplate.getForObject(
                accountServiceUrl + "/api/accounts/" + accountNumber,
                AccountResponse.class
        );
    }

    public void updateBalance(String accountNumber, java.math.BigDecimal newBalance) {
        restTemplate.put(
                accountServiceUrl + "/api/accounts/" + accountNumber + "/balance",
                newBalance
        );
    }
}