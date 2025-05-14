package hu.morkalla.instantpayment.exception;

public class TransactionAlreadyProcessed extends RuntimeException {
    public TransactionAlreadyProcessed(String message) {
        super(message);
    }

}
