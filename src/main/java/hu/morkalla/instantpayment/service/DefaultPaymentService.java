package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Account;
import hu.morkalla.instantpayment.domain.Transaction;
import hu.morkalla.instantpayment.exception.AccountBalanceHasChanged;
import hu.morkalla.instantpayment.exception.EntityNotFoundException;
import hu.morkalla.instantpayment.exception.NotEnoughBalance;
import hu.morkalla.instantpayment.exception.TransactionAlreadyProcessed;
import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    @Retryable(retryFor = AccountBalanceHasChanged.class,
            notRecoverable = {NotEnoughBalance.class, TransactionAlreadyProcessed.class, EntityNotFoundException.class},
            backoff = @Backoff(delay = 100))
    public void transfer(PaymentRequestDto paymentRequestDto) {

        final String transactionId = paymentRequestDto.transactionId();

        validateTransactionUnique(transactionId);

        Account sourceAccount =
                accountService.findAccountByAccountNumber(paymentRequestDto.sourceAccountNumber());
        Account targetAccount =
                accountService.findAccountByAccountNumber(paymentRequestDto.targetAccountNumber());

        BigDecimal transactionAmount = paymentRequestDto.transactionAmount();
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(transactionAmount);

        if (isSourceBalanceValid(newSourceBalance)) {
            sourceAccount.setBalance(newSourceBalance);
            targetAccount.setBalance(targetAccount.getBalance().add(transactionAmount));

            try {
                accountService.saveAccount(sourceAccount);
                accountService.saveAccount(targetAccount);
            } catch (StaleObjectStateException exception) {
                throw new AccountBalanceHasChanged("Account balance has changed", sourceAccount.getId(), targetAccount.getId());
            }

            Transaction completedTransaction = new Transaction(null, transactionId, sourceAccount.getId(), targetAccount.getId(), transactionAmount, paymentRequestDto.transactionDate(), "COMPLETED");
            transactionService.saveTransaction(completedTransaction);
            notificationService.sendNotification(completedTransaction);
        } else {
            throw new NotEnoughBalance("Not enough balance for transaction: " + transactionId);
        }
    }

    @Recover
    public void recover(AccountBalanceHasChanged exception, PaymentRequestDto paymentRequestDto) {
        transactionService.saveTransaction(
                new Transaction(null, paymentRequestDto.transactionId(),
                        exception.getSourceAccountId(),
                        exception.getTargetAccountId(),
                        paymentRequestDto.transactionAmount(), paymentRequestDto.transactionDate(), "FAILED"));
    }

    private void validateTransactionUnique(String transactionId) {
        if (transactionService.existsByTransactionId(transactionId)) {
            throw new TransactionAlreadyProcessed("Transaction already processed: " + transactionId);
        }
    }

    private static boolean isSourceBalanceValid(BigDecimal newSourceBalance) {
        return newSourceBalance.compareTo(BigDecimal.ZERO) > 0;
    }
}
