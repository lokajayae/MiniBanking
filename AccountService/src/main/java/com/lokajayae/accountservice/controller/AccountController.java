package com.lokajayae.accountservice.controller;

import  com.lokajayae.accountservice.dto.response.ApiResponse;
import com.lokajayae.accountservice.dto.response.AccountResponse;
import com.lokajayae.accountservice.dto.request.CreateAccountRequest;
import com.lokajayae.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @RequestBody @Valid CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(accountService.createAccount(request), "Success create account"));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                accountService.getAccountByNumber(accountNumber), "Success get account information"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsWithMinBalance(
            @RequestParam BigDecimal minBalance) {
        return ResponseEntity.ok(ApiResponse.success(
                accountService.getAccountsWithMinBalance(minBalance), "Success get account information"));
    }

    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<AccountResponse>> updateBalance(
            @PathVariable String accountNumber,
            @RequestBody BigDecimal newBalance) {
        return ResponseEntity.ok(ApiResponse.success(
                accountService.updateBalance(accountNumber, newBalance), "Success get account information"));
    }
}