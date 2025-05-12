package hu.morkalla.instantpayment.rest;

import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import hu.morkalla.instantpayment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    public void whenInvokePay_thenPaymentServiceInvoked() {
        PaymentRequestDto requestDto = new PaymentRequestDto("1", "2", LocalDateTime.now(), BigDecimal.ONE);

        paymentController.pay(requestDto);

        Mockito.verify(paymentService).pay(requestDto);
    }

}
