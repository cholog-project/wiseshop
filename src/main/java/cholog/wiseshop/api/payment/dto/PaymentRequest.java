package cholog.wiseshop.api.payment.dto;

public record PaymentRequest(String orderId,
                             Long amount,
                             String paymentKey) {

}
