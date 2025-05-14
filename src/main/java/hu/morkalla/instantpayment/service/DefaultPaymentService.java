package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Account;
import hu.morkalla.instantpayment.domain.Transaction;
import hu.morkalla.instantpayment.exception.NotEnoughBalance;
import hu.morkalla.instantpayment.exception.TransactionAlreadyProcessed;
import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Override
    @Transactional
    @Retryable(retryFor = StaleObjectStateException.class)
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

            accountService.saveAccount(sourceAccount);
            accountService.saveAccount(targetAccount);

            transactionService.saveTransaction(
                    new Transaction(null, transactionId, sourceAccount.getId(), targetAccount.getId(), transactionAmount, paymentRequestDto.transactionDate(), "COMPLETED"));
        } else {
            throw new NotEnoughBalance("Not enough balance for transaction: " + transactionId);
        }

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
