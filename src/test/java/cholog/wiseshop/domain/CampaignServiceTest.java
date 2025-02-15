package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.CampaignFixture;
import cholog.wiseshop.fixture.MemberFixture;
import cholog.wiseshop.fixture.ProductFixture;
import cholog.wiseshop.fixture.ProductFixture.Request;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
public class CampaignServiceTest extends BaseTest {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Nested
    class 캠페인을_생성한다 {

        @Test
        void 캠페인_시작날짜가_24시간보다_이전이면_예외() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Member member = memberRepository.save(MemberFixture.최준호());
            CreateCampaignRequest request = new CreateCampaignRequest(
                now.minusHours(25),
                now.minusHours(23),
                90,
                Request.보약_생성_요청()
            );

            // when & then
            assertThatThrownBy(
                () -> campaignService.createCampaign(request, member, now))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.CAMPAIGN_INVALID_START_DATE.getMessage());
        }

        @Test
        void 총_재고가_목표_수량_이하면_예외() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Member member = memberRepository.save(MemberFixture.최준호());
            CreateProductRequest productRequest = new CreateProductRequest(
                "보약",
                "먹으면 기분이 좋아져요.",
                10000,
                100
            );
            CreateCampaignRequest campaignRequest = new CreateCampaignRequest(
                now.plusHours(1),
                now.plusHours(2),
                100,
                productRequest
            );

            // when & then
            assertThatThrownBy(
                () -> campaignService.createCampaign(campaignRequest, member, now))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.INVALID_GOAL_QUANTITY.getMessage());
        }

        @Test
        void 캠페인_시작날짜가_종료날짜_보다_이후면_예외() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Member member = memberRepository.save(MemberFixture.최준호());
            CreateCampaignRequest request = new CreateCampaignRequest(
                now.plusHours(2),
                now.plusHours(1),
                90,
                Request.보약_생성_요청()
            );

            // when & then
            assertThatThrownBy(
                () -> campaignService.createCampaign(request, member, now))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.CAMPAIGN_INVALID_DATE_RANGE.getMessage());
        }

        @Test
        void 현재_시간이_시작시간_이후_종료시간_이전일_때_상태변경() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Member member = memberRepository.save(MemberFixture.최준호());
            LocalDateTime startDate = now.plusHours(1);
            LocalDateTime endDate = now.plusHours(2);
            Campaign campaign = new Campaign(startDate, endDate, 90, member, now);

            // when
            campaign.setState(now.minusHours(1), endDate);

            // then
            assertThat(campaign.getState()).isEqualTo(CampaignState.IN_PROGRESS);
        }

        @Test
        void 현재_시간이_시작시간_이전일_때_상태설정() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Member member = memberRepository.save(MemberFixture.최준호());
            LocalDateTime startDate = now.plusHours(1);
            LocalDateTime endDate = now.plusHours(2);

            // when
            Campaign campaign = new Campaign(startDate, endDate, 90, member, now);

            // then
            assertThat(campaign.getState()).isEqualTo(CampaignState.WAITING);
        }

        @Test
        void 종료_후_목표수량_미달성_시_상태설정() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Campaign campaign = Campaign.builder()
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .goalQuantity(5)
                .soldQuantity(2)
                .now(now)
                .build();

            // when
            campaign.setStateWhenFinish();

            // then
            assertThat(campaign.getState()).isEqualTo(CampaignState.FAILED);
        }

        @Test
        void 종료_후_목표수량_달성_시_상태설정() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Campaign campaign = Campaign.builder()
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .goalQuantity(5)
                .soldQuantity(5)
                .now(now)
                .build();

            // when
            campaign.setStateWhenFinish();

            // then
            assertThat(campaign.getState()).isEqualTo(CampaignState.SUCCESS);
        }

        @Test
        void 캠페인_전체조회_시_진행중_캠페인들만_조회() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Stock stockA = new Stock(10);
            Stock stockB = new Stock(10);
            Stock stockC = new Stock(10);
            stockRepository.saveAll(List.of(stockA, stockB, stockC));
            Campaign campaignA = Campaign.builder()
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .goalQuantity(5)
                .state(CampaignState.WAITING)
                .now(now)
                .build();
            Product productA = new Product("product", "zzang", 2000, campaignA, stockA);

            Campaign campaignB = Campaign.builder()
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .goalQuantity(5)
                .state(CampaignState.WAITING)
                .now(now)
                .build();
            Product productB = new Product("product", "zzang", 2000, campaignB, stockB);

            Campaign waitingCampaign = Campaign.builder()
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .goalQuantity(5)
                .state(CampaignState.WAITING)
                .now(now)
                .build();
            Product productC = new Product("product", "zzang", 2000, waitingCampaign, stockC);

            campaignA.setState(now.minusHours(1), now.plusHours(2));
            campaignB.setState(now.minusHours(1), now.plusHours(2));
            campaignRepository.saveAll(List.of(campaignA, campaignB, waitingCampaign));
            productRepository.saveAll(List.of(productA, productB, productC));

            // when
            List<ReadCampaignResponse> response = campaignService.readInProgressCampaign();

            // then
            assertThat(response).hasSize(2);
        }

        @Test
        void 캠페인_단건_조회() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Campaign campaign = campaignRepository.save(CampaignFixture.진행중인_보약_캠페인(member));
            Stock stock = new Stock(20);
            stockRepository.save(stock);
            Product product = productRepository.save(ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock));

            // when
            ReadCampaignResponse response = campaignService.readCampaign(campaign.getId());

            // then
            assertThat(response.campaignId()).isEqualTo(campaign.getId());
            assertThat(response.product().id()).isEqualTo(product.getId());
        }

        @Test
        void 재고수량이_1개_미만이면_예외() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Member member = memberRepository.save(MemberFixture.최준호());
            CreateCampaignRequest request = new CreateCampaignRequest(
                now.plusDays(2),
                now.plusDays(2).plusHours(10),
                1000,
                new CreateProductRequest(
                    "보약",
                    "먹으면 기분이 좋아져요.",
                    10000,
                    0
                )
            );

            // when & then
            assertThatThrownBy(
                () -> campaignService.createCampaign(request, member, now))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.INVALID_GOAL_QUANTITY.getMessage());
        }
    }
}
