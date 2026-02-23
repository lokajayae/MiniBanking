package com.lokajayae.transactionservice.controller;

import com.lokajayae.transactionservice.dto.request.TransactionRequest;
import com.lokajayae.transactionservice.dto.response.TransactionResponse;
import com.lokajayae.transactionservice.dto.response.ApiResponse;
import com.lokajayae.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getTransactionById(id), "Success Get Transaction Information"));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transactionService.deposit(request), "Success deposit money"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transactionService.withdraw(request), "Success withdraw money"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(transactionService.transfer(request), "Success transfer money"));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getHistory(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getTransactionHistory(accountNumber),"Success get transaction history"));
    }
}