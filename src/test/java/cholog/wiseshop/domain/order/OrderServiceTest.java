package cholog.wiseshop.domain.order;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.response.OrderResponse;
import cholog.wiseshop.api.order.service.OrderService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.ProductRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Long campaignId;

    private CreateProductRequest request;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        campaignRepository.deleteAll();

        request = getCreateProductRequest();

        LocalDateTime startDate = LocalDateTime.now().plus(50, ChronoUnit.MILLIS);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(5);
        int goalQuantity = 5;

        campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
    }

    @Test
    void 주문_생성_성공() throws InterruptedException {
        //given
        int orderQuantity = 5;
        CreateOrderRequest orderRequest = new CreateOrderRequest(campaignId, orderQuantity);

        Thread.sleep(100);

        //when
        Long orderId = orderService.createOrder(orderRequest);
        OrderResponse response = orderService.readOrder(orderId);

        //then
        assertThat(response.productName()).isEqualTo(request.name());
        assertThat(response.count()).isEqualTo(orderQuantity);
    }
}
