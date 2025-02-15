package cholog.wiseshop.domain;

import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    class 사용자가_주문을_수행한다 {

    }

    @Nested
    class 사용자가_주문_취소를_수행한다 {

    }
}
