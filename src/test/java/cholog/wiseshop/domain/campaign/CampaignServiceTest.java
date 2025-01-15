package cholog.wiseshop.domain.campaign;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.domain.product.ProductRepositoryTest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        campaignRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void 캠페인_추가하기() {
        //given
        CreateProductRequest request = getCreateProductRequest();

        Long productId = productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30, 10);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30, 10);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();

        //then
        assertThat(findCampaign.getProduct().getId()).isEqualTo(productId);
        assertThat(findCampaign.getStartDate().isEqual(startDate)).isTrue();
        assertThat(findCampaign.getEndDate().isEqual(endDate)).isTrue();
        assertThat(findCampaign.getGoalQuantity()).isEqualTo(goalQuantity);
        assertThat(findCampaign.getState()).isEqualTo(CampaignState.WAITING);
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

    @Test
    void 캠페인_조회하기() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        ReadCampaignResponse response = campaignService.readCampaign(campaignId);

        //then
        assertThat(response.campaignId()).isEqualTo(campaignId);
        assertThat(response.productId()).isEqualTo(productId);
    }

    @Test
    void 캠페인_조회하기_예외_잘못된_캠페인ID() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Long wrongId = campaignId + 1;

        //then
        assertThatThrownBy(() -> campaignService.readCampaign(wrongId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 캠페인_시작_상태_변경_성공() throws InterruptedException {
        //given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(10);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();

        // then
        Awaitility.await()
                .atLeast(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Campaign modifiedCampaign = campaignRepository.findById(findCampaign.getId())
                            .orElseThrow();
                    assertThat(modifiedCampaign.getState()).isEqualTo(CampaignState.IN_PROGRESS);
                });
    }

    @Test
    void 캠페인_실패_상태_변경_성공() throws InterruptedException {
        //given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(1);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();

        // then
        Awaitility.await()
                .atLeast(1,TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Campaign modifiedCampaign = campaignRepository.findById(findCampaign.getId())
                            .orElseThrow();
                    assertThat(modifiedCampaign.getState()).isEqualTo(CampaignState.FAILED);
                });
    }

    @Test
    @DisplayName("전달받은 날짜와 캠페인의 시작날짜를 비교해 현재 캠페인이 시작됐는지를 확인합니다.")
    void 캠페인_시작날짜_비교() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime requestDate = startDate.plusSeconds(3);
        LocalDateTime endDate = startDate.plusSeconds(5);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        boolean isStarted = campaignService.isStarted(campaignId, requestDate);

        // then
        assertThat(isStarted).isTrue();
    }
}
