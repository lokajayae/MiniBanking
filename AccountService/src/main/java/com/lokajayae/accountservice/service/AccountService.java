package com.lokajayae.accountservice.service;

import com.lokajayae.accountservice.dto.response.AccountResponse;
import com.lokajayae.accountservice.dto.request.CreateAccountRequest;
import com.lokajayae.accountservice.model.Account;
import com.lokajayae.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = Account.builder()
                .name(request.getName())
                .accountNumber(generateAccountNumber())
                .balance(request.getInitialBalance())
                .build();

        return mapToResponse(accountRepository.save(account));
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    public List<AccountResponse> getAccountsWithMinBalance(BigDecimal minBalance) {
        return accountRepository.findAccountsWithMinBalance(minBalance)
                .stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(AccountResponse::getBalance).reversed())
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}