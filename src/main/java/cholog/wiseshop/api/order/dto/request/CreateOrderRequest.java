package cholog.wiseshop.api.order.dto.request;

import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.product.Product;

public record CreateOrderRequest(Long productId, int count) {
    public Order from(Product product) {
        return new Order(
                product,
                this.count
        );
    }
}
