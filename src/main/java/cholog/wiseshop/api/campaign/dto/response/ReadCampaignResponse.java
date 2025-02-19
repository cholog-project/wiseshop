package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.campaign.CampaignState;

public record ReadCampaignResponse(
    Long campaignId,
    String startDate,
    String endDate,
    int goalQuantity,
    int orderedQuantity,
    int stockQuantity,
    CampaignState state,
    ProductResponse product,
    Long ownerId
) {

}
