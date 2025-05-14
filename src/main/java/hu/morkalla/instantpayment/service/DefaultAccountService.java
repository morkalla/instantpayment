package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Account;
import hu.morkalla.instantpayment.exception.EntityNotFoundException;
import hu.morkalla.instantpayment.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new EntityNotFoundException("Account not found by account number: " + accountNumber));
    }
}
