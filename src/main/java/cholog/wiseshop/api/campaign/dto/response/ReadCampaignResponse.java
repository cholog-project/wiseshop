package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.product.Product;

public record ReadCampaignResponse(
    Long campaignId,
    String startDate,
    String endDate,
    int goalQuantity,
    ProductResponse product
) {

    public static ReadCampaignResponse of(Product product, Campaign campaign) {
        return new ReadCampaignResponse(
            campaign.getId(),
            campaign.getStartDate().toString(),
            campaign.getEndDate().toString(),
            campaign.getGoalQuantity(),
            new ProductResponse(product));
    }
}
