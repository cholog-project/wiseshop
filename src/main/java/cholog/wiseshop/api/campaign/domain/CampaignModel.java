package cholog.wiseshop.api.campaign.domain;

import cholog.wiseshop.api.member.domain.MemberModel;
import cholog.wiseshop.api.product.domain.ProductModel;

import java.time.LocalDateTime;
import java.util.List;

public record CampaignModel(
        Long goalQuantity,
        LocalDateTime startDate,
        LocalDateTime endDate,
        CampaignStatus status,
        MemberModel supplier,
        List<ProductModel> products
) {

}
