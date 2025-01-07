package cholog.wiseshop.api.campaign.dto;

import java.time.LocalDateTime;

public record CreateCampaignRequest(LocalDateTime startDate,
                                    LocalDateTime endDate,
                                    int goalQuantity,
                                    Long productId) {
}
