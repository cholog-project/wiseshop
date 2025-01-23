package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.api.product.dto.response.ProductResponse;

public record ReadCampaignResponse(Long campaignId,
                                    String startDate,
                                    String endDate,
                                    Integer goalQuantity,
                                   ProductResponse product) {
}
