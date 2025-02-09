package cholog.wiseshop.domain.campaign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.ProductFixture;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CampaignService campaignService;

    @MockitoBean
    private ThreadTaskScheduler scheduler;

    @AfterEach
    public void cleanUp() {
        productRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
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
        doNothing().when(scheduler).scheduleCampaign(new Campaign());
        CreateCampaignResponse campaign = campaignService.createCampaign(request, member);

        // then
        assertThat(campaignRepository.findById(campaign.campaignId())).isNotEmpty();
        List<Product> products = productRepository.findProductsByCampaignId(campaign.campaignId());
        assertThat(products).isNotEmpty();
        assertThat(products.getFirst().getStock()).isNotNull();
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
        doNothing().when(scheduler).scheduleCampaign(new Campaign());

        CreateCampaignResponse campaign = campaignService.createCampaign(request, member);
        Campaign findCampaign = campaignRepository.findById(campaign.campaignId()).orElseThrow();

        // then
        assertThat(findCampaign.getStartDate()).isEqualTo(startTime);
        assertThat(findCampaign.getEndDate()).isEqualTo(endTime);
        assertThat(findCampaign.getGoalQuantity()).isEqualTo(10);
    }


    @Test
    void 캠페인_조회하기() {
        // given
        CreateProductRequest request = ProductFixture.getCreateProductRequest();
        Member member = new Member("123@123.123", "김수민", "비밀번호");
        memberRepository.save(member);
        Stock stock = stockRepository.save(new Stock(request.totalQuantity()));
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        Campaign campaign = campaignRepository.save(new Campaign(
            startDate,
            endDate,
            2,
            member
        ));
        productRepository.save(new Product(
            request.name(),
            request.description(),
            request.price(),
            campaign,
            stock
        ));

        // when
        ReadCampaignResponse response = campaignService.readCampaign(campaign.getId());

        // then
        assertAll(
            () -> assertThat(response.campaignId()).isEqualTo(campaign.getId()),
            () -> assertThat(response.product().name()).isEqualTo(request.name()),
            () -> assertThat(response.product().description()).isEqualTo(request.description()),
            () -> assertThat(response.product().price()).isEqualTo(request.price()),
            () -> assertThat(response.product().totalQuantity()).isEqualTo(request.totalQuantity())
        );
    }

    @Test
    void 캠페인_조회하기_예외_잘못된_캠페인ID() {
        // given
        CreateProductRequest request = ProductFixture.getCreateProductRequest();
        Member member = new Member("123@123.123", "김수민", "비밀번호");
        memberRepository.save(member);
        Stock stock = stockRepository.save(new Stock(request.totalQuantity()));
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        Campaign campaign = campaignRepository.save(new Campaign(
            startDate,
            endDate,
            2,
            member
        ));
        productRepository.save(new Product(
            request.name(),
            request.description(),
            request.price(),
            campaign,
            stock
        ));
        Long wrongId = campaign.getId() + 1;

        // then
        assertThatThrownBy(() -> campaignService.readCampaign(wrongId))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.CAMPAIGN_NOT_FOUND.getMessage());
    }

    @Test
    void 기간_내_캠페인_전체조회_확인() {
        // given
        setData();

        // when
        List<ReadCampaignResponse> result = campaignService.readAllCampaign();

        // then
        assertThat(result).hasSize(5);
    }

    void setData() {
        CreateProductRequest request = ProductFixture.getCreateProductRequest();
        Member member = new Member("123@123.123", "김수민", "비밀번호");
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        for (int i = 0; i < 5; i++) {
            memberRepository.save(member);
            Stock stock = stockRepository.save(new Stock(request.totalQuantity()));
            Campaign campaign = campaignRepository.save(new Campaign(
                startDate,
                endDate,
                2,
                member
            ));
            productRepository.save(new Product(
                request.name(),
                request.description(),
                request.price(),
                campaign,
                stock
            ));
        }
    }
}
