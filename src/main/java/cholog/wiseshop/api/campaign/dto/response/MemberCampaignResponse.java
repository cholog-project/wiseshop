package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.db.campaign.Campaign;
import java.util.List;

public record MemberCampaignResponse(
    List<Long> campaignId
) {

    public static MemberCampaignResponse from(List<Campaign> campaigns) {
        List<Long> campaignIds = campaigns.stream().
            map(Campaign::getId)
            .toList();
        return new MemberCampaignResponse(campaignIds);
    }
}
