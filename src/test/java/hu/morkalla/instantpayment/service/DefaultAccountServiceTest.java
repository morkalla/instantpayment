package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.domain.Account;
import hu.morkalla.instantpayment.exception.EntityNotFoundException;
import hu.morkalla.instantpayment.repository.AccountRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DefaultAccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DefaultAccountService defaultAccountService;


    @Test
    public void givenValidAccount_whenFindAccountByAccountNumber_thenReturnAccount() {
        Account validAccount = new Account(1L, "123", BigDecimal.ONE, 1);

        Mockito.when(accountRepository.findByAccountNumber("123")).thenReturn(Optional.of(validAccount));

        Account actualAccount = defaultAccountService.findAccountByAccountNumber("123");

        Assertions.assertThat(actualAccount).isEqualTo(validAccount);
        Mockito.verify(accountRepository).findByAccountNumber("123");
    }

    @Test
    public void givenInvalidAccount_whenFindAccountByAccountNumber_thenReturnException() {
        Mockito.when(accountRepository.findByAccountNumber("123")).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                ThrowableAssert.catchThrowableOfType(EntityNotFoundException.class,
                        () -> defaultAccountService.findAccountByAccountNumber("123"));


        Assertions.assertThat(exception.getMessage()).isEqualTo("Account not found by account number: 123");
        Mockito.verify(accountRepository).findByAccountNumber("123");
    }

    @Test
    public void whenSaveAccount_thenRepositoryInvoked() {
        Account validAccount = new Account(1L, "123", BigDecimal.ONE, 1);

        defaultAccountService.saveAccount(validAccount);

        Mockito.verify(accountRepository).save(validAccount);
    }

}
