package com.lokajayae.transactionservice.service;

import com.lokajayae.transactionservice.client.AccountClient;
import com.lokajayae.transactionservice.dto.response.AccountResponse;
import com.lokajayae.transactionservice.dto.request.TransactionRequest;
import com.lokajayae.transactionservice.dto.response.TransactionResponse;
import com.lokajayae.transactionservice.model.Transaction;
import com.lokajayae.transactionservice.model.TransactionStatus;
import com.lokajayae.transactionservice.model.TransactionType;
import com.lokajayae.transactionservice.repository.TransactionRepository;
import com.lokajayae.transactionservice.producer.TransactionProducer;
import com.lokajayae.transactionservice.event.TransactionEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final TransactionProducer transactionProducer;

    public TransactionResponse deposit(TransactionRequest request) {
        AccountResponse account = accountClient.getAccount(request.getToAccountNumber());

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        accountClient.updateBalance(request.getToAccountNumber(), newBalance);

        Transaction transaction = Transaction.builder()
                .toAccountNumber(request.getToAccountNumber())
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        publishEvent(saved);

        return mapToResponse(saved);
    }

    public TransactionResponse withdraw(TransactionRequest request) {
        AccountResponse account = accountClient.getAccount(request.getFromAccountNumber());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        accountClient.updateBalance(request.getFromAccountNumber(), newBalance);

        Transaction transaction = Transaction.builder()
                .fromAccountNumber(request.getFromAccountNumber())
                .amount(request.getAmount())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        publishEvent(saved);

        return mapToResponse(saved);
    }

    public TransactionResponse transfer(TransactionRequest request) {
        AccountResponse fromAccount = accountClient.getAccount(request.getFromAccountNumber());
        AccountResponse toAccount = accountClient.getAccount(request.getToAccountNumber());

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        accountClient.updateBalance(request.getFromAccountNumber(),
                fromAccount.getBalance().subtract(request.getAmount()));
        accountClient.updateBalance(request.getToAccountNumber(),
                toAccount.getBalance().add(request.getAmount()));

        Transaction outTransaction = Transaction.builder()
                .fromAccountNumber(request.getFromAccountNumber())
                .toAccountNumber(request.getToAccountNumber())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER_OUT)
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        Transaction inTransaction = Transaction.builder()
                .fromAccountNumber(request.getFromAccountNumber())
                .toAccountNumber(request.getToAccountNumber())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER_IN)
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        Transaction savedOut = transactionRepository.save(outTransaction);
        transactionRepository.save(inTransaction);
        publishEvent(savedOut);

        return mapToResponse(savedOut);
    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        return transactionRepository.findTransactionHistory(accountNumber)
                .stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getLargeTransactions(BigDecimal minAmount) {
        return transactionRepository.findLargeTransactions(minAmount)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public TransactionResponse getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToResponse(transaction);
    }

    private void publishEvent(Transaction transaction) {
        transactionProducer.publishTransaction(TransactionEvent.builder()
                .transactionId(transaction.getId())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .createdAt(transaction.getCreatedAt())
                .build());
    }
}