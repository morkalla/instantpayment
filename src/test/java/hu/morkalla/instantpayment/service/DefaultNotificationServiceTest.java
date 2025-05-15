package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class DefaultNotificationServiceTest {

    @Mock
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @InjectMocks
    private DefaultNotificationService defaultNotificationService;

    @Test
    public void whenSendNotification_thenNotificationSent() {
        Transaction transaction = new Transaction(1L, "id", 1L, 2L, BigDecimal.ONE, LocalDateTime.now(), "status");

        defaultNotificationService.sendNotification(transaction);

        Mockito.verify(kafkaTemplate).send("notifications", transaction);
    }

}
