package cholog.wiseshop.api.order.dto.response;

import cholog.wiseshop.db.order.Order;
import java.time.LocalDateTime;

public record OrderResponse(Long id,
                            Long productId,
                            int count,
                            LocalDateTime createdDate,
                            LocalDateTime modifiedDate) {

    public OrderResponse(Order order) {
        this(
                order.getId(),
                order.getProduct().getId(),
                order.getCount(),
                order.getCreatedDate(),
                order.getModifiedDate()
        );
    }
}
