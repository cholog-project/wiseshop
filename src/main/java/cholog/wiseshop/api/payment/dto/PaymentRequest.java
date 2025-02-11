package cholog.wiseshop.api.payment.dto;

public record PaymentRequest(String paymentOrderId,
                             Long amount,
                             String paymentKey) {

}
