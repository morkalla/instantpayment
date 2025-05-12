package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import org.springframework.stereotype.Service;

@Service
public class DefaultPaymentService implements PaymentService {
    @Override
    public void pay(PaymentRequestDto paymentRequestDto) {

    }
}
