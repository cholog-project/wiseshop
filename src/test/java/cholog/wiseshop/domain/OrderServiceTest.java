package cholog.wiseshop.domain;

import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Nested
    class 사용자가_주문을_수행한다 {

        @Test
        void 사용자는_주문을_정상적으로_수행한다() {

        }

        @Test
        void 상품_정보가_존재하지_않으면_예외() {

        }

        @Test
        void 캠페인이_진행중이지_않으면_예외() {

        }

        @Test
        void 주문_가능한_수량을_초과하면_예외() {

        }

        @Test
        void 본인의_캠페인을_주문하면_예외() {

        }
    }

    @Nested
    class 사용자가_주문_조회를_수행한다 {

        @Test
        void 사용자는_본인의_주문목록을_조회한다() {

        }

        @Test
        void 사용자는_주문을_상세조회_한다() {

        }

        @Test
        void 주문이_존재하지_않으면_예외() {

        }
    }

    @Nested
    class 사용자가_주문_취소를_수행한다 {

        @Test
        void 사용자는_주문을_정상적으로_취소한다() {

        }

        @Test
        void 주문_정보가_존재하지_않으면_예외() {

        }

        @Test
        void 캠페인이_진행중이지_않으면_예외() {

        }
    }
}
