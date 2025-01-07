package cholog.wiseshop.domain.campaign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.campaign.dto.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.ProductRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CampaignServiceTest {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void 캠페인_추가하기() {
        //given
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;
        CreateProductRequest productRequest = new CreateProductRequest(name, description, price);
        Long productId = productService.createProduct(productRequest);

        //when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();

        //then
        assertThat(findCampaign.getProduct().getId()).isEqualTo(productId);
        assertThat(findCampaign.getStartDate().isEqual(startDate)).isTrue();
        assertThat(findCampaign.getEndDate().isEqual(endDate)).isTrue();
        assertThat(findCampaign.getGoalQuantity()).isEqualTo(goalQuantity);
    }

    @Test
    void 캠페인_추가하기_예외_잘못된_상품ID() {
        //given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;
        Long productId = 1L;

        //when & then
        assertThatThrownBy(() -> campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
