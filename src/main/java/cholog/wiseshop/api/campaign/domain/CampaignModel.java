package cholog.wiseshop.api.campaign.domain;

import cholog.wiseshop.api.member.domain.MemberModel;
import cholog.wiseshop.api.product.domain.ProductModel;

import java.time.LocalDateTime;
import java.util.List;

public class CampaignModel {
    private final Long goalQuantity;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final CampaignStatus status;
    private final MemberModel supplier;
    private final List<ProductModel> products;

    public CampaignModel(Long goalQuantity, LocalDateTime startDate, LocalDateTime endDate, CampaignStatus status, MemberModel supplier, List<ProductModel> products) {
        this.goalQuantity = goalQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.supplier = supplier;
        this.products = products;
    }

    public Long getGoalQuantity() {
        return goalQuantity;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public MemberModel getSupplier() {
        return supplier;
    }

    public List<ProductModel> getProducts() {
        return products;
    }
}
