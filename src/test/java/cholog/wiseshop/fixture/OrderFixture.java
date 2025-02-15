package cholog.wiseshop.fixture;

import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.product.Product;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderFixture {

    public static Order 주문하기(Product product, Member member, Address address) {
        return Order.builder()
            .count(1)
            .product(product)
            .member(member)
            .address(address.getRoadAddress() + " " + address.getDetailAddress())
            .build();
    }
}
