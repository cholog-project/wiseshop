package cholog.wiseshop.api.order.dto.request;

import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.product.Product;

public record CreateOrderRequest(
    Long productId,
    Long addressId,
    int orderQuantity
) {

    public Order from(Product product, Member member, Address address) {
        return new Order(
            0,
            product,
            member,
            address.getRoadAddress() + " " + address.getDetailAddress()
        );
    }
}
