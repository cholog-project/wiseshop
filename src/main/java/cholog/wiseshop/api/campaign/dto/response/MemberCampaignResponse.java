package cholog.wiseshop.api.campaign.dto.response;

import java.util.List;

public record MemberCampaignResponse(
    List<Long> campaignId
) {

    public static MemberCampaignResponse from(List<Long> campaignIds) {
        return new MemberCampaignResponse(campaignIds);
    }
}
