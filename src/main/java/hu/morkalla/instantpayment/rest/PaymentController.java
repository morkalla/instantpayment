package hu.morkalla.instantpayment.rest;

import hu.morkalla.instantpayment.rest.domain.PaymentRequestDto;
import hu.morkalla.instantpayment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pay")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Handle payment request")
    @PostMapping
    public void pay(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        paymentService.transfer(paymentRequestDto);
    }
}
