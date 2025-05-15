package hu.morkalla.instantpayment.exception;

import lombok.Getter;

@Getter
public class AccountBalanceHasChanged extends RuntimeException {

    private final Long sourceAccountId;

    private final Long targetAccountId;

    public AccountBalanceHasChanged(String message, Long sourceAccountId, Long targetAccountId) {
        super(message);
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

}
