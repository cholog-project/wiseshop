package cholog.wiseshop.api.campaign.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;

    public CampaignService(CampaignRepository campaignRepository, ProductRepository productRepository) {
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
    }

    public Long createCampaign(CreateCampaignRequest request) {
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Campaign savedCampaign = campaignRepository.save(
                new Campaign(findProduct, request.startDate(), request.endDate(), request.goalQuantity()));
        return savedCampaign.getId();
    }

    @Transactional(readOnly = true)
    public ReadCampaignResponse readCampaign(Long id) {
        Campaign findCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("캠페인이 존재하지 않습니다."));
        return new ReadCampaignResponse(findCampaign.getId(), findCampaign.getProduct().getId());
    }
}
