package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.api.campaign.domain.CampaignModel;
import cholog.wiseshop.api.product.domain.ProductModel;
import cholog.wiseshop.api.product.dto.response.ProductResponse;

public record ReadCampaignResponse(
    Long campaignId,
    String startDate,
    String endDate,
    long goalQuantity,
    ProductResponse product
) {

    public static ReadCampaignResponse of(ProductModel product, CampaignModel campaign) {
        return new ReadCampaignResponse(
            campaign.id(),
            campaign.startDate().toString(),
            campaign.endDate().toString(),
            campaign.goalQuantity(),
            new ProductResponse(product)
        );
    }
}
