package cholog.wiseshop.api.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
    @NotBlank(message = "주문 번호는 필수입니다.")
    String orderId,

    @NotBlank(message = "결제 키는 필수입니다.")
    String paymentKey,

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    Long amount
) {

}
