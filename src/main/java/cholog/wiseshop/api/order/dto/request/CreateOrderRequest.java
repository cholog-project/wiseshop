package cholog.wiseshop.api.order.dto.request;

import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.product.Product;

public record CreateOrderRequest(
    Integer count,
    Long productId,
    Long addressId,
    int orderQuantity
) {

    public Order from(Product product, Member member, Address address) {
        return new Order(
            count,
            product,
            member,
            address.getRoadAddress() + " " + address.getDetailAddress()
        );
    }
}
