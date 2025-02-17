package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.product.Product;

public record MemberCampaignResponse(
    Long campaignId,
    String startDate,
    String endDate,
    int goalQuantity,
    int orderedQuantity,
    CampaignState state,
    MemberProductResponse product
) {

    public static MemberCampaignResponse of(Campaign campaign, Product product) {
        return new MemberCampaignResponse(
            campaign.getId(),
            campaign.getStartDate().toString(),
            campaign.getEndDate().toString(),
            campaign.getGoalQuantity(),
            campaign.getSoldQuantity(),
            campaign.getState(),
            MemberProductResponse.from(product)
        );
    }
}
