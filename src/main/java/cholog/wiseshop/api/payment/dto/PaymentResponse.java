package cholog.wiseshop.api.payment.dto;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record PaymentResponse(
    String paymentKey,
    String type,
    String orderId,
    String orderName,
    String state,
    Long totalAmount,
    @DateTimeFormat(iso = ISO.DATE_TIME)
    LocalDateTime requestedAt,
    @DateTimeFormat(iso = ISO.DATE_TIME)
    LocalDateTime approvedAt
) {

}
