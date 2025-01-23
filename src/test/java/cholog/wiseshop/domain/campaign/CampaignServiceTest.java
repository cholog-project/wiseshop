package cholog.wiseshop.domain.campaign;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.AllCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.product.ProductRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
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
        campaignRepository.deleteAll();
    }

    @Test
    void 캠페인_추가하기() {
        //given
        CreateProductRequest request = getCreateProductRequest();

        //when
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        Integer goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();

        //then
        assertThat(findCampaign.getStartDate().truncatedTo(ChronoUnit.SECONDS))
            .isEqualTo(startDate.truncatedTo(ChronoUnit.SECONDS));
        assertThat(findCampaign.getEndDate().truncatedTo(ChronoUnit.SECONDS))
            .isEqualTo(endDate.truncatedTo(ChronoUnit.SECONDS));
        assertThat(findCampaign.getGoalQuantity()).isEqualTo(goalQuantity);
        assertThat(findCampaign.getState()).isEqualTo(CampaignState.WAITING);
    }


    @Test
    void 캠페인_조회하기() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
        ReadCampaignResponse response = campaignService.readCampaign(campaignId);

        //then
        assertAll(
            () -> assertThat(response.campaignId()).isEqualTo(campaignId),
            () -> assertThat(response.product().name()).isEqualTo(request.name()),
            () -> assertThat(response.product().description()).isEqualTo(request.description()),
            () -> assertThat(response.product().price()).isEqualTo(request.price()),
            () -> assertThat(response.product().totalQuantity()).isEqualTo(request.totalQuantity())
        );
    }

    @Test
    void 캠페인_조회하기_예외_잘못된_캠페인ID() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
        Long wrongId = campaignId + 1;

        //then
        assertThatThrownBy(() -> campaignService.readCampaign(wrongId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 캠페인_시작_상태_변경_성공() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(10);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
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
    void 캠페인_실패_상태_변경_성공() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(1);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();

        // then
        Awaitility.await()
            .atLeast(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Campaign modifiedCampaign = campaignRepository.findById(findCampaign.getId())
                    .orElseThrow();
                assertThat(modifiedCampaign.getState()).isEqualTo(CampaignState.FAILED);
            });
    }

    @Test
    void 캠페인이_시작됐는지_확인() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        productService.createProduct(request);

        //when
        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(10);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request));
        // then
        Awaitility.await()
            .atLeast(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(campaignService.isStarted(campaignId)).isTrue());
    }

    @Test
    void 기간_내_캠페인_전체조회_확인() {
        //given
        CreateProductRequest request = getCreateProductRequest();
        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(2);
        int goalQuantity = 5;
        campaignService.createCampaign(new CreateCampaignRequest(startDate, endDate, goalQuantity, request));

        //when
        List<AllCampaignResponse> result = campaignService.readAllCampaign();

        //then
        Awaitility.await()
            .atMost(101, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertThat(result).hasSize(1));
    }
}
