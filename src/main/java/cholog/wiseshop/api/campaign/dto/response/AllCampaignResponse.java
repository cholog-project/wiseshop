package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.product.Product;
import java.time.LocalDateTime;
import java.util.List;

public record AllCampaignResponse(List<ReadCampaignResponse> responses) {

}
