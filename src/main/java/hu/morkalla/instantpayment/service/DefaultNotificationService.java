package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {

    private static final String TOPIC = "notifications";
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Async
    public void sendNotification(Transaction transaction) {
        kafkaTemplate.send(TOPIC, transaction);
    }
}
