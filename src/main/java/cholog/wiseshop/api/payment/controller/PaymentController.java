package cholog.wiseshop.api.payment.controller;

import cholog.wiseshop.api.payment.dto.PaymentRequest;
import cholog.wiseshop.api.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<Long> confirmPayment(
        @RequestBody PaymentRequest paymentRequest
    ) {
        var paymentId = paymentService.confirmPayment(paymentRequest);
        return ResponseEntity.ok(paymentId);
    }
}
