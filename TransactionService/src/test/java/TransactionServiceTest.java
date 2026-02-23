import com.lokajayae.transactionservice.service.TransactionService;
import com.lokajayae.transactionservice.client.AccountClient;
import com.lokajayae.transactionservice.dto.response.AccountResponse;
import com.lokajayae.transactionservice.dto.request.TransactionRequest;
import com.lokajayae.transactionservice.dto.response.TransactionResponse;
import com.lokajayae.transactionservice.model.Transaction;
import com.lokajayae.transactionservice.model.TransactionStatus;
import com.lokajayae.transactionservice.model.TransactionType;
import com.lokajayae.transactionservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private TransactionService transactionService;

    private AccountResponse mockFromAccount;
    private AccountResponse mockToAccount;
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        mockFromAccount = new AccountResponse();
        mockFromAccount.setAccountNumber("ACC_FROM");
        mockFromAccount.setBalance(new BigDecimal("1000000"));

        mockToAccount = new AccountResponse();
        mockToAccount.setAccountNumber("ACC_TO");
        mockToAccount.setBalance(new BigDecimal("500000"));

        mockTransaction = Transaction.builder()
                .id("txn-uuid-001")
                .fromAccountNumber("ACC_FROM")
                .toAccountNumber("ACC_TO")
                .amount(new BigDecimal("200000"))
                .type(TransactionType.TRANSFER_OUT)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // --- DEPOSIT ---

    @Test
    void deposit_shouldIncreaseBalance_andReturnResponse() {
        TransactionRequest request = new TransactionRequest();
        request.setToAccountNumber("ACC_TO");
        request.setAmount(new BigDecimal("200000"));
        request.setDescription("Top up");

        Transaction depositTxn = Transaction.builder()
                .id("txn-deposit-001")
                .toAccountNumber("ACC_TO")
                .amount(new BigDecimal("200000"))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountClient.getAccount("ACC_TO")).thenReturn(mockToAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(depositTxn);

        TransactionResponse response = transactionService.deposit(request);

        assertNotNull(response);
        assertEquals(TransactionType.DEPOSIT, response.getType());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
        assertEquals(new BigDecimal("200000"), response.getAmount());
        verify(accountClient, times(1)).updateBalance("ACC_TO", new BigDecimal("700000"));
    }

    // --- WITHDRAW ---

    @Test
    void withdraw_shouldDecreaseBalance_andReturnResponse() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("ACC_FROM");
        request.setAmount(new BigDecimal("200000"));
        request.setDescription("ATM withdrawal");

        Transaction withdrawTxn = Transaction.builder()
                .id("txn-withdraw-001")
                .fromAccountNumber("ACC_FROM")
                .amount(new BigDecimal("200000"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountClient.getAccount("ACC_FROM")).thenReturn(mockFromAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawTxn);

        TransactionResponse response = transactionService.withdraw(request);

        assertNotNull(response);
        assertEquals(TransactionType.WITHDRAWAL, response.getType());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
        verify(accountClient, times(1)).updateBalance("ACC_FROM", new BigDecimal("800000"));
    }

    @Test
    void withdraw_shouldThrowException_whenInsufficientBalance() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("ACC_FROM");
        request.setAmount(new BigDecimal("9999999"));

        when(accountClient.getAccount("ACC_FROM")).thenReturn(mockFromAccount);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.withdraw(request));

        assertEquals("Insufficient balance", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    // --- TRANSFER ---

    @Test
    void transfer_shouldMoveFunds_andReturnResponse() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("ACC_FROM");
        request.setToAccountNumber("ACC_TO");
        request.setAmount(new BigDecimal("200000"));
        request.setDescription("Payment");

        when(accountClient.getAccount("ACC_FROM")).thenReturn(mockFromAccount);
        when(accountClient.getAccount("ACC_TO")).thenReturn(mockToAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        TransactionResponse response = transactionService.transfer(request);

        assertNotNull(response);
        assertEquals(TransactionType.TRANSFER_OUT, response.getType());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
        verify(accountClient).updateBalance("ACC_FROM", new BigDecimal("800000"));
        verify(accountClient).updateBalance("ACC_TO", new BigDecimal("700000"));
    }

    @Test
    void transfer_shouldThrowException_whenInsufficientBalance() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountNumber("ACC_FROM");
        request.setToAccountNumber("ACC_TO");
        request.setAmount(new BigDecimal("9999999"));

        when(accountClient.getAccount("ACC_FROM")).thenReturn(mockFromAccount);
        when(accountClient.getAccount("ACC_TO")).thenReturn(mockToAccount);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(request));

        assertEquals("Insufficient balance", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    // --- HISTORY ---

    @Test
    void getTransactionHistory_shouldReturnSortedSuccessTransactions() {
        Transaction oldTxn = Transaction.builder()
                .id("txn-old")
                .fromAccountNumber("ACC_FROM")
                .amount(new BigDecimal("100000"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        Transaction newTxn = Transaction.builder()
                .id("txn-new")
                .fromAccountNumber("ACC_FROM")
                .amount(new BigDecimal("200000"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findTransactionHistory("ACC_FROM"))
                .thenReturn(List.of(oldTxn, newTxn));

        List<TransactionResponse> responses = transactionService
                .getTransactionHistory("ACC_FROM");

        assertNotNull(responses);
        assertEquals(2, responses.size());
        // verify sorted newest first
        assertTrue(responses.get(0).getCreatedAt()
                .isAfter(responses.get(1).getCreatedAt()));
    }

    @Test
    void getTransactionHistory_shouldExcludeFailedTransactions() {
        Transaction failedTxn = Transaction.builder()
                .id("txn-failed")
                .fromAccountNumber("ACC_FROM")
                .amount(new BigDecimal("100000"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findTransactionHistory("ACC_FROM"))
                .thenReturn(List.of(failedTxn));

        List<TransactionResponse> responses = transactionService
                .getTransactionHistory("ACC_FROM");

        assertTrue(responses.isEmpty());
    }
}