package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Account;
import hu.morkalla.instantpayment.domain.Transaction;
import hu.morkalla.instantpayment.exception.AccountBalanceHasChanged;
import hu.morkalla.instantpayment.exception.EntityNotFoundException;
import hu.morkalla.instantpayment.exception.NotEnoughBalance;
import hu.morkalla.instantpayment.exception.TransactionAlreadyProcessed;
import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultPaymentServiceTest {

    private static final PaymentRequestDto PAYMENT_REQUEST_DTO =
            new PaymentRequestDto("transactionId", "sourceAccountNumber", "targetAccountNumber", LocalDateTime.now(), BigDecimal.ONE);

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DefaultPaymentService defaultPaymentService;

    @Test
    public void givenProcessedTransactionId_whenTransfer_thenReturnThrowException() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(true);


        TransactionAlreadyProcessed exception =
                ThrowableAssert.catchThrowableOfType(TransactionAlreadyProcessed.class,
                        () -> defaultPaymentService.transfer(PAYMENT_REQUEST_DTO));


        Assertions.assertThat(exception.getMessage()).isEqualTo("Transaction already processed: " + PAYMENT_REQUEST_DTO.transactionId());
        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService, never()).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService, never()).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService, never()).saveAccount(any());
        verify(transactionService, never()).saveTransaction(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void givenMissingSourceAccount_whenTransfer_thenReturnThrowException() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(false);
        when(accountService.findAccountByAccountNumber("sourceAccountNumber")).thenThrow(new EntityNotFoundException("account missing"));


        EntityNotFoundException exception =
                ThrowableAssert.catchThrowableOfType(EntityNotFoundException.class,
                        () -> defaultPaymentService.transfer(PAYMENT_REQUEST_DTO));


        Assertions.assertThat(exception.getMessage()).isEqualTo("account missing");
        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService, never()).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService, never()).saveAccount(any());
        verify(transactionService, never()).saveTransaction(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void givenMissingTargetAccount_whenTransfer_thenReturnThrowException() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(false);
        when(accountService.findAccountByAccountNumber("sourceAccountNumber")).thenReturn(new Account(1L, "sourceAccountNumber", BigDecimal.ZERO, 1));
        when(accountService.findAccountByAccountNumber("targetAccountNumber")).thenThrow(new EntityNotFoundException("account missing"));


        EntityNotFoundException exception =
                ThrowableAssert.catchThrowableOfType(EntityNotFoundException.class,
                        () -> defaultPaymentService.transfer(PAYMENT_REQUEST_DTO));


        Assertions.assertThat(exception.getMessage()).isEqualTo("account missing");
        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService, never()).saveAccount(any());
        verify(transactionService, never()).saveTransaction(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void givenValidAccountsWithEnoughBalance_whenTransfer_thenTransferCompleted() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(false);
        when(accountService.findAccountByAccountNumber("sourceAccountNumber")).thenReturn(new Account(1L, "sourceAccountNumber", BigDecimal.TWO, 1));
        when(accountService.findAccountByAccountNumber("targetAccountNumber")).thenReturn(new Account(2L, "targetAccountNumber", BigDecimal.ZERO, 1));

        defaultPaymentService.transfer(PAYMENT_REQUEST_DTO);

        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService).saveAccount(new Account(1L, "sourceAccountNumber", BigDecimal.ONE, 1));
        verify(accountService).saveAccount(new Account(2L, "targetAccountNumber", BigDecimal.ONE, 1));
        Transaction transaction = new Transaction(null, PAYMENT_REQUEST_DTO.transactionId(), 1L, 2L, PAYMENT_REQUEST_DTO.transactionAmount(), PAYMENT_REQUEST_DTO.transactionDate(), "COMPLETED");
        verify(transactionService).saveTransaction(transaction);
        verify(notificationService).sendNotification(transaction);
    }


    @Test
    public void givenSourceAccountBalanceWithNotEnoughBalance_whenTransfer_thenReturnThrowException() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(false);
        when(accountService.findAccountByAccountNumber("sourceAccountNumber")).thenReturn(new Account(1L, "sourceAccountNumber", BigDecimal.ZERO, 1));
        when(accountService.findAccountByAccountNumber("targetAccountNumber")).thenReturn(new Account(2L, "targetAccountNumber", BigDecimal.ZERO, 1));


        NotEnoughBalance exception =
                ThrowableAssert.catchThrowableOfType(NotEnoughBalance.class,
                        () -> defaultPaymentService.transfer(PAYMENT_REQUEST_DTO));


        Assertions.assertThat(exception.getMessage()).isEqualTo("Not enough balance for transaction: " + PAYMENT_REQUEST_DTO.transactionId());
        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService, never()).saveAccount(any());
        verify(transactionService, never()).saveTransaction(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void givenSourceAccountChanged_whenTransfer_thenReturnThrowException() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(false);
        when(accountService.findAccountByAccountNumber("sourceAccountNumber")).thenReturn(new Account(1L, "sourceAccountNumber", BigDecimal.TWO, 1));
        when(accountService.findAccountByAccountNumber("targetAccountNumber")).thenReturn(new Account(2L, "targetAccountNumber", BigDecimal.ZERO, 1));
        doThrow(new StaleObjectStateException(null, null)).when(accountService).saveAccount(new Account(1L, "sourceAccountNumber", BigDecimal.ONE, 1));


        AccountBalanceHasChanged exception =
                ThrowableAssert.catchThrowableOfType(AccountBalanceHasChanged.class,
                        () -> defaultPaymentService.transfer(PAYMENT_REQUEST_DTO));


        Assertions.assertThat(exception.getMessage()).isEqualTo("Account balance has changed");
        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService, times(1)).saveAccount(any());
        verify(transactionService, never()).saveTransaction(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void givenTargetAccountChanged_whenTransfer_thenReturnThrowException() {
        when(transactionService.existsByTransactionId("transactionId")).thenReturn(false);
        when(accountService.findAccountByAccountNumber("sourceAccountNumber")).thenReturn(new Account(1L, "sourceAccountNumber", BigDecimal.TWO, 1));
        when(accountService.findAccountByAccountNumber("targetAccountNumber")).thenReturn(new Account(2L, "targetAccountNumber", BigDecimal.ZERO, 1));
        doNothing().when(accountService).saveAccount(new Account(1L, "sourceAccountNumber", BigDecimal.ONE, 1));
        doThrow(new StaleObjectStateException(null, null)).when(accountService).saveAccount(new Account(2L, "targetAccountNumber", BigDecimal.ONE, 1));

        AccountBalanceHasChanged exception =
                ThrowableAssert.catchThrowableOfType(AccountBalanceHasChanged.class,
                        () -> defaultPaymentService.transfer(PAYMENT_REQUEST_DTO));


        Assertions.assertThat(exception.getMessage()).isEqualTo("Account balance has changed");
        verify(transactionService).existsByTransactionId("transactionId");
        verify(accountService).findAccountByAccountNumber("sourceAccountNumber");
        verify(accountService).findAccountByAccountNumber("targetAccountNumber");
        verify(accountService, times(2)).saveAccount(any());
        verify(transactionService, never()).saveTransaction(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    public void whenRecover_thenTransactionCreated() {
        defaultPaymentService.recover(new AccountBalanceHasChanged("message", 1L, 2L), PAYMENT_REQUEST_DTO);

        verify(transactionService).saveTransaction(new Transaction(null, PAYMENT_REQUEST_DTO.transactionId(),
                1L,
                2L,
                PAYMENT_REQUEST_DTO.transactionAmount(), PAYMENT_REQUEST_DTO.transactionDate(), "FAILED"));
    }

}
