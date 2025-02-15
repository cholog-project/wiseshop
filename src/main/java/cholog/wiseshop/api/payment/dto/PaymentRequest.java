package cholog.wiseshop.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentRequest(@JsonProperty("orderId") String paymentOrderId,
                             Long amount,
                             String paymentKey) {

}
