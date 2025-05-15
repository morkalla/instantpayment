package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Transaction;

public interface NotificationService {

    void sendNotification(Transaction transaction);

}
