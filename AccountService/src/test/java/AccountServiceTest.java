
import com.lokajayae.accountservice.service.AccountService;
import com.lokajayae.accountservice.dto.response.AccountResponse;
import com.lokajayae.accountservice.dto.request.CreateAccountRequest;
import com.lokajayae.accountservice.model.Account;
import com.lokajayae.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account mockAccount;

    @BeforeEach
    void setUp() {
        mockAccount = Account.builder()
                .id("uuid-001")
                .name("John Doe")
                .accountNumber("ACC123456")
                .balance(new BigDecimal("1000000"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createAccount_shouldReturnAccountResponse() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setName("John Doe");
        request.setInitialBalance(new BigDecimal("1000000"));

        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals(new BigDecimal("1000000"), response.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountByNumber_shouldReturnAccount_whenExists() {
        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(mockAccount));

        AccountResponse response = accountService.getAccountByNumber("ACC123456");

        assertNotNull(response);
        assertEquals("ACC123456", response.getAccountNumber());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void getAccountByNumber_shouldThrowException_whenNotFound() {
        when(accountRepository.findByAccountNumber("INVALID"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getAccountByNumber("INVALID"));

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void updateBalance_shouldUpdateAndReturnAccount() {
        BigDecimal newBalance = new BigDecimal("2000000");
        mockAccount.setBalance(newBalance);

        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        AccountResponse response = accountService.updateBalance("ACC123456", newBalance);

        assertNotNull(response);
        assertEquals(newBalance, response.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateBalance_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByAccountNumber("INVALID"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.updateBalance("INVALID", new BigDecimal("1000")));

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void getAccountsWithMinBalance_shouldReturnFilteredList() {
        Account richAccount = Account.builder()
                .id("uuid-002")
                .name("Rich Guy")
                .accountNumber("ACC999999")
                .balance(new BigDecimal("5000000"))
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findAccountsWithMinBalance(new BigDecimal("500000")))
                .thenReturn(List.of(mockAccount, richAccount));

        List<AccountResponse> responses = accountService
                .getAccountsWithMinBalance(new BigDecimal("500000"));

        assertNotNull(responses);
        assertEquals(2, responses.size());
        // verify sorted by balance descending
        assertTrue(responses.get(0).getBalance()
                .compareTo(responses.get(1).getBalance()) >= 0);
    }
}