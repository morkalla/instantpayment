package hu.morkalla.instantpayment.service;

import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;

public interface PaymentService {

    void transfer(PaymentRequestDto paymentRequestDto);

}
