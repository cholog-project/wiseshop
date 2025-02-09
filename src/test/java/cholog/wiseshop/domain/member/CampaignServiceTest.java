package cholog.wiseshop.domain.member;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
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
            Member member = memberRepository.save(MemberFixture.최준호());
            CreateCampaignRequest request = new CreateCampaignRequest(
                LocalDateTime.now().minusHours(25),
                LocalDateTime.now().minusHours(23),
                100,
                Request.보약_생성_요청()
            );

            // when & then
            Assertions.assertThatThrownBy(() -> campaignService.createCampaign(request, member))
                .isInstanceOf(WiseShopException.class);
        }

        @Test
        void 재고수량이_1개_미만이면_예외() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            CreateCampaignRequest request = new CreateCampaignRequest(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(10),
                0,
                Request.보약_생성_요청()
            );

            // when & then
            Assertions.assertThatThrownBy(() -> campaignService.createCampaign(request, member))
                .isInstanceOf(WiseShopException.class);
        }
    }
}
