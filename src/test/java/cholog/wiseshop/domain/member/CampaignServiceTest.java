package cholog.wiseshop.domain.member;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.MemberFixture;
import cholog.wiseshop.fixture.ProductFixture.Request;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
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
                100,
                Request.보약_생성_요청()
            );

            // when & then
            Assertions.assertThatThrownBy(
                    () -> campaignService.createCampaign(request, member, now))
                .isInstanceOf(WiseShopException.class);
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
            Assertions.assertThatThrownBy(
                    () -> campaignService.createCampaign(request, member, now))
                .isInstanceOf(WiseShopException.class);
        }
    }
}
