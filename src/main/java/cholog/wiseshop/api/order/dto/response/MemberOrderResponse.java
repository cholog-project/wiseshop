package cholog.wiseshop.api.order.dto.response;

import cholog.wiseshop.db.order.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record MemberOrderResponse(
    Long id,
    Long productId,
    String productName,
    String address,
    int count,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdDate,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime modifiedDate
) {

    public MemberOrderResponse(Order order) {
        this(
            order.getId(),
            order.getProduct().getId(),
            order.getProduct().getName(),
            order.getAddress(),
            order.getCount(),
            order.getCreatedDate(),
            order.getModifiedDate()
        );
    }
}
