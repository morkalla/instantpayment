package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Account;

public interface AccountService {

    Account findAccountByAccountNumber(String accountNumber);

}
