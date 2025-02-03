package cholog.wiseshop.domain.order;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.response.OrderResponse;
import cholog.wiseshop.api.order.service.OrderService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrderServiceTest extends BaseTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderService orderService;

    private Campaign campaign;

    private Product product;

    private Member member;

    private CreateProductRequest productRequest;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRequest = getCreateProductRequest();
        Stock stock = stockRepository.save(new Stock(productRequest.totalQuantity()));
        product = productRepository.save(new Product(
            productRequest.name(), productRequest.description(), productRequest.price(), stock));
        member = memberRepository.save(new Member("test@naver.com", "초록", "qwer"));
        campaign = campaignRepository.save(new Campaign(null, null, 5, member));
        product.addCampaign(campaign);
    }

    @Test
    void 주문_생성_및_조회_성공() {
        // given
        int orderQuantity = 5;
        Long productId = product.getId();
        CreateOrderRequest orderRequest = new CreateOrderRequest(productId, orderQuantity);
        campaign.updateState(CampaignState.IN_PROGRESS);
        Member orderMember = memberRepository.save(new Member("test2@naver.com", "초록2", "qwer2"));

        // when
        Long orderId = orderService.createOrder(orderRequest, orderMember);
        OrderResponse response = orderService.readOrder(orderId);

        // then
        assertThat(response.productName()).isEqualTo(productRequest.name());
        assertThat(response.count()).isEqualTo(orderQuantity);
    }

    @Test
    void 본인이_생성한_상품_주문_예외() {
        // given
        int orderQuantity = 5;
        Long productId = product.getId();
        CreateOrderRequest orderRequest = new CreateOrderRequest(productId, orderQuantity);
        campaign.updateState(CampaignState.IN_PROGRESS);

        // when
        assertThatThrownBy(() -> orderService.createOrder(orderRequest, member))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ORDER_NOT_AVAILABLE.getMessage());
    }

    @Test
    void 주문_목록_조회_성공() {
        // given
        int orderQuantity = 1;
        Long productId = product.getId();
        CreateOrderRequest orderRequest = new CreateOrderRequest(productId, orderQuantity);
        Member orderMember = memberRepository.save(new Member("test2@naver.com", "초록2", "qwer2"));
        campaign.updateState(CampaignState.IN_PROGRESS);

        //when
        orderService.createOrder(orderRequest, orderMember);
        List<OrderResponse> response = orderService.readMemberOrders(orderMember);

        //then
        assertThat(response.size()).isEqualTo(1L);
    }
}
