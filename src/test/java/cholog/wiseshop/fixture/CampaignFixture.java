package cholog.wiseshop.fixture;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class CampaignFixture {

    public static Campaign 보약_캠페인(Member member) {
        // TODO: https://sokdak-sokdak.tistory.com/10
        LocalDateTime now = LocalDateTime.now();
        return Campaign.builder()
            .startDate(now.minusDays(3))
            .endDate(now.plusDays(5))
            .goalQuantity(100)
            .soldQuantity(0)
            .member(member)
            .build();
    }

    public static Campaign 진행중인_보약_캠페인(Member member) {
        LocalDateTime now = LocalDateTime.now();
        return Campaign.builder()
            .startDate(now.plusDays(1))
            .endDate(now.plusDays(2))
            .goalQuantity(100)
            .soldQuantity(0)
            .state(CampaignState.IN_PROGRESS)
            .member(member)
            .now(now)
            .build();
    }
}
