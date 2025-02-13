package cholog.wiseshop.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.MemberFixture;
import cholog.wiseshop.fixture.ProductFixture.Request;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
public class CampaignServiceTest extends BaseTest {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private MemberRepository memberRepository;

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
