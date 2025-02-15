package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.response.MemberOrderResponse;
import cholog.wiseshop.api.order.service.OrderService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.AddressFixture;
import cholog.wiseshop.fixture.CampaignFixture;
import cholog.wiseshop.fixture.MemberFixture;
import cholog.wiseshop.fixture.OrderFixture;
import cholog.wiseshop.fixture.ProductFixture;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class OrderServiceTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Nested
    class 사용자가_주문을_수행한다 {

        @Test
        void 사용자는_주문을_정상적으로_수행하고_본인의_주문목록을_조회한다() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);

            CreateOrderRequest request = new CreateOrderRequest(
                1,
                product.getId(),
                address.getId(),
                10
            );

            // when
            orderService.createOrder(request, junesoo);

            // then
            assertThat(orderRepository.findByMemberId(junesoo.getId())).isNotEmpty();
        }

        @Test
        void 상품_정보가_존재하지_않으면_예외() {
            /// given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Long invalidProductId = 1L;

            CreateOrderRequest request = new CreateOrderRequest(
                1,
                invalidProductId,
                address.getId(),
                10
            );

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request, junesoo))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        void 배송_정보가_존재하지_않으면_예외() {
            /// given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);

            Long invalidAddressId = 1L;

            CreateOrderRequest request = new CreateOrderRequest(
                1,
                product.getId(),
                invalidAddressId,
                10
            );

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request, junesoo))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        @Test
        void 캠페인이_진행중이지_않으면_예외() {
            /// given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaign.updateState(CampaignState.SUCCESS);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);

            CreateOrderRequest request = new CreateOrderRequest(
                1,
                product.getId(),
                address.getId(),
                10
            );

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request, junesoo))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.CAMPAIGN_NOT_IN_PROGRESS.getMessage());
        }

        @Test
        void 주문_가능한_수량을_초과하면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);

            CreateOrderRequest request = new CreateOrderRequest(
                1,
                product.getId(),
                address.getId(),
                201
            );

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request, junesoo))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(String.format(WiseShopErrorCode.ORDER_LIMIT_EXCEED.getMessage(), 200));
        }

        @Test
        void 본인의_캠페인을_주문하면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            memberRepository.save(junho);
            Address address = AddressFixture.집주소(junho);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);

            CreateOrderRequest request = new CreateOrderRequest(
                1,
                product.getId(),
                address.getId(),
                10
            );

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request, junho))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ORDER_NOT_AVAILABLE.getMessage());
        }
    }

    @Nested
    class 사용자가_주문_조회를_수행한다 {

        @Test
        void 사용자는_본인의_주문목록을_조회한다() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);
            Order order = OrderFixture.주문하기(product, junesoo, address);
            orderRepository.save(order);

            // when
            var response = orderService.readMemberOrders(junesoo);
            MemberOrderResponse orderResponse = response.getFirst();

            // then
            assertThat(orderResponse.address()).isEqualTo(order.getAddress());
            assertThat(orderResponse.count()).isEqualTo(order.getCount());
        }

        @Test
        void 사용자는_주문을_상세조회_한다() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);
            Order order = OrderFixture.주문하기(product, junesoo, address);
            orderRepository.save(order);

            // when
            MemberOrderResponse response = orderService.readOrder(junesoo, order.getId());

            // then
            assertThat(response.address()).isEqualTo(order.getAddress());
            assertThat(response.count()).isEqualTo(order.getCount());
        }

        @Test
        void 주문이_존재하지_않으면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);
            Long invalidOrderId = 1L;

            // when & then
            assertThatThrownBy(() -> orderService.readOrder(junesoo, invalidOrderId))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ORDER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 사용자가_주문_취소를_수행한다 {

        @Test
        void 사용자는_주문을_정상적으로_취소한다() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);
            Order order = OrderFixture.주문하기(product, junesoo, address);
            Order junesooOrder = orderRepository.save(order);

            // when
            orderService.deleteOrder(junesoo, junesooOrder.getId());

            // then
            assertThat(orderRepository.findById(junesooOrder.getId())).isEmpty();
        }

        @Test
        void 주문_정보가_존재하지_않으면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);
            Long invalidOrderId = 999999L;

            // when & then
            assertThatThrownBy(() -> orderService.deleteOrder(junho, invalidOrderId))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        @Test
        void 캠페인이_진행중이지_않으면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            Member junesoo = MemberFixture.김준수();
            memberRepository.saveAll(List.of(junho, junesoo));
            Address address = AddressFixture.집주소(junesoo);
            addressRepository.save(address);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(junho);
            campaign.updateState(CampaignState.SUCCESS);
            campaignRepository.save(campaign);
            Stock stock = new Stock(200);
            stockRepository.save(stock);
            Product product = ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, junho);
            productRepository.save(product);
            Order order = OrderFixture.주문하기(product, junesoo, address);
            Order junesooOrder = orderRepository.save(order);

            // when & then
            assertThatThrownBy(() -> orderService.deleteOrder(junesoo, junesooOrder.getId()))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.CAMPAIGN_NOT_IN_PROGRESS.getMessage());
        }
    }
}
