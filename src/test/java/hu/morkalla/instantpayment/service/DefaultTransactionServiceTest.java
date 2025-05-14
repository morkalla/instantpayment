package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Transaction;
import hu.morkalla.instantpayment.repository.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class DefaultTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DefaultTransactionService defaultTransactionService;


    @Test
    public void whenExistsByTransactionId_thenReturnFromRepository() {
        Mockito.when(transactionRepository.existsByTransactionId("123")).thenReturn(true);

        boolean response = defaultTransactionService.existsByTransactionId("123");

        Assertions.assertThat(response).isEqualTo(true);
        Mockito.verify(transactionRepository).existsByTransactionId("123");
    }

    @Test
    public void whenSaveTransaction_thenRepositoryInvoked() {
        Transaction validTransaction = new Transaction(1L, "123", 1L, 2L, BigDecimal.ONE,
                LocalDateTime.of(2025, 5, 14, 12, 12, 12), "COMPLETED");

        defaultTransactionService.saveTransaction(validTransaction);

        Mockito.verify(transactionRepository).save(validTransaction);
    }

}
