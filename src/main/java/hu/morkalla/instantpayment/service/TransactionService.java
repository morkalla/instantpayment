package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Transaction;

public interface TransactionService {

    boolean existsByTransactionId(String transactionId);

    void saveTransaction(Transaction transaction);

}
