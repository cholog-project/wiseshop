package cholog.wiseshop.api.campaign.dto.response;

public record CreateCampaignResponse(
    Long campaignId
) {

    public static CreateCampaignResponse from(Long campaignId) {
        return new CreateCampaignResponse(campaignId);
    }
}
