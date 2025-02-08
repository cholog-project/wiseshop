package cholog.wiseshop.domain.campaign;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.CreateCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.common.ThreadTaskScheduler;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class CampaignServiceTest extends BaseTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
//    private ProductService productService;

    @Autowired
    private CampaignService campaignService;

    @MockitoBean
    private ThreadTaskScheduler scheduler;

    @AfterEach
    public void cleanUp() {
        productRepository.deleteAllInBatch();
        campaignRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    void 캠페인_생성_성공() {
        // given
        Member member = new Member("123@123.123", "김수민", "비밀번호");
        memberRepository.save(member);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        CreateCampaignRequest request = new CreateCampaignRequest(
            startTime,
            endTime,
            10,
            new CreateProductRequest("상품이름", "상품설명", 2000, 20)
        );

        // when
        doNothing().when(scheduler).scheduleCampaignByDate(new Campaign());
        CreateCampaignResponse campaign = campaignService.createCampaign(request, member);

        // then
        assertThat(campaignRepository.findById(campaign.campaignId())).isNotEmpty();
        List<Product> product = productRepository.findProductsByCampaignId(campaign.campaignId());
        assertThat(product).isNotEmpty();
        assertThat(product.getFirst().getStock()).isNotNull();
    }

    @Test
    void 캠페인_상세_정보_검증하기() {
        // given
        Member member = new Member("123@123.123", "김수민", "비밀번호");
        memberRepository.save(member);
        LocalDateTime startTime = LocalDateTime.of(2025, 3, 3, 3, 3);
        LocalDateTime endTime = LocalDateTime.of(2025, 4, 4, 4, 4);
        CreateCampaignRequest request = new CreateCampaignRequest(
            startTime,
            endTime,
            10,
            new CreateProductRequest("상품이름", "상품설명", 2000, 20)
        );

        // when
        doNothing().when(scheduler).scheduleCampaignByDate(new Campaign());

        CreateCampaignResponse campaign = campaignService.createCampaign(request, member);
        Campaign findCampaign = campaignRepository.findById(campaign.campaignId()).orElseThrow();

        // then
        assertThat(findCampaign.getStartDate()).isEqualTo(startTime);
        assertThat(findCampaign.getEndDate()).isEqualTo(endTime);
        assertThat(findCampaign.getGoalQuantity()).isEqualTo(10);
    }

//
//    @Test
//    void 캠페인_조회하기() {
//        // given
//        CreateProductRequest request = getCreateProductRequest();
//
//        // when
//        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
//        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
//        int goalQuantity = 5;
//
//        Long campaignId = campaignService.createCampaign(
//            new CreateCampaignRequest(startDate, endDate, goalQuantity, request), null);
//        ReadCampaignResponse response = campaignService.readCampaign(campaignId);
//
//        // then
//        assertAll(
//            () -> assertThat(response.campaignId()).isEqualTo(campaignId),
//            () -> assertThat(response.product().name()).isEqualTo(request.name()),
//            () -> assertThat(response.product().description()).isEqualTo(request.description()),
//            () -> assertThat(response.product().price()).isEqualTo(request.price()),
//            () -> assertThat(response.product().totalQuantity()).isEqualTo(request.totalQuantity())
//        );
//    }
//
//    @Test
//    void 캠페인_조회하기_예외_잘못된_캠페인ID() {
//        // given
//        CreateProductRequest request = getCreateProductRequest();
//
//        // when
//        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
//        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
//        int goalQuantity = 5;
//
//        Long campaignId = campaignService.createCampaign(
//            new CreateCampaignRequest(startDate, endDate, goalQuantity, request), null);
//        Long wrongId = campaignId + 1;
//
//        // then
//        assertThatThrownBy(() -> campaignService.readCampaign(wrongId))
//            .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    void 캠페인_시작_상태_변경_성공() {
//        // given
//        CreateProductRequest request = getCreateProductRequest();
//
//        // when
//        LocalDateTime startDate = LocalDateTime.now().plus(50, ChronoUnit.MILLIS);
//        LocalDateTime endDate = LocalDateTime.now().plusMinutes(10);
//        int goalQuantity = 5;
//
//        Long campaignId = campaignService.createCampaign(
//            new CreateCampaignRequest(startDate, endDate, goalQuantity, request), null);
//
//        // then
//        Awaitility.await()
//            .dontCatchUncaughtExceptions()
//            .until(() -> {
//                Campaign modifiedCampaign = campaignRepository.findById(campaignId)
//                    .orElseThrow();
//                return modifiedCampaign.getState().equals(CampaignState.IN_PROGRESS);
//            });
//    }
//
//    @Test
//    void 캠페인_실패_상태_변경_성공() {
//        // given
//        CreateProductRequest request = getCreateProductRequest();
//
//        // when
//        LocalDateTime startDate = LocalDateTime.now();
//        LocalDateTime endDate = LocalDateTime.now().plus(100, ChronoUnit.MILLIS);
//        int goalQuantity = 5;
//
//        Long campaignId = campaignService.createCampaign(
//            new CreateCampaignRequest(startDate, endDate, goalQuantity, request), null);
//        Campaign findCampaign = campaignRepository.findById(campaignId).orElseThrow();
//
//        // then
//        Awaitility.await()
//            .dontCatchUncaughtExceptions()
//            .atMost(200, TimeUnit.MILLISECONDS)
//            .untilAsserted(() -> {
//                Campaign modifiedCampaign = campaignRepository.findById(findCampaign.getId())
//                    .orElseThrow();
//                assertThat(modifiedCampaign.getState()).isEqualTo(CampaignState.FAILED);
//            });
//    }
//
//    @Test
//    void 캠페인이_시작됐는지_확인() {
//        // given
//        CreateProductRequest request = getCreateProductRequest();
//        productService.createProduct(request);
//
//        // when
//        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
//        LocalDateTime endDate = LocalDateTime.now().plusSeconds(10);
//        int goalQuantity = 5;
//
//        Long campaignId = campaignService.createCampaign(
//            new CreateCampaignRequest(startDate, endDate, goalQuantity, request), null);
//        // then
//        Awaitility.await()
//            .atLeast(1, TimeUnit.SECONDS)
//            .untilAsserted(() -> assertThat(campaignService.isStarted(campaignId)).isTrue());
//    }

    @Test
    void 기간_내_캠페인_전체조회_확인() {
        // given
        CreateProductRequest request = getCreateProductRequest();
        LocalDateTime startDate = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDate = LocalDateTime.now().plusSeconds(2);
        int goalQuantity = 5;
        campaignService.createCampaign(
            new CreateCampaignRequest(startDate, endDate, goalQuantity, request), null);

        // when
        List<ReadCampaignResponse> result = campaignService.readAllCampaign();

        // then
        Awaitility.await()
            .atMost(101, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertThat(result).hasSize(1));
    }
}
