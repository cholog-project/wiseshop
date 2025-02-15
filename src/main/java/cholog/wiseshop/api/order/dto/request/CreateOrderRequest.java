package cholog.wiseshop.api.order.dto.request;

import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.product.Product;

public record CreateOrderRequest(Long productId,
                                 int orderQuantity,
                                 String paymentOrderId,
                                 Long amount,
                                 String paymentKey) {

    public Order from(Product product, Member member) {
        return new Order(
            0,
            product,
            member,
            ""
        );
    }
}
