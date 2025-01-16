package cholog.wiseshop.api.order.dto;

public record CreateOrderRequest(Long productId,
                                 int count) {
}
