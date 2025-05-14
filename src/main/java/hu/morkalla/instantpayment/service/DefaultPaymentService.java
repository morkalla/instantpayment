package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private final AccountService accountService;

    @Override
    public void transfer(PaymentRequestDto paymentRequestDto) {

    }
}
