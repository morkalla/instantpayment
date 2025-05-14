package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Transaction;
import hu.morkalla.instantpayment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public boolean existsByTransactionId(String transactionId) {
        return transactionRepository.existsByTransactionId(transactionId);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
