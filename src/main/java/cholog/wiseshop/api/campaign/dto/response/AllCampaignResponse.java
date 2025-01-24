package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.product.Product;
import java.time.LocalDateTime;

public record AllCampaignResponse(Long campaignId,
                                  String productName,
                                  String productDescription,
                                  int goalQuantity,
                                  LocalDateTime startDate,
                                  LocalDateTime endDate) {

    public static AllCampaignResponse from(Product product, Campaign campaign) {
        return new AllCampaignResponse(
            campaign.getId(),
            product.getName(),
            product.getDescription(),
            campaign.getGoalQuantity(),
            campaign.getStartDate(),
            campaign.getEndDate()
        );
    }
}
