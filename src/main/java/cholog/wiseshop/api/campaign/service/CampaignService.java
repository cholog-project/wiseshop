package cholog.wiseshop.api.campaign.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.api.campaign.dto.response.CreateCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.common.ThreadTaskScheduler;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ThreadTaskScheduler scheduler;

    public CampaignService(CampaignRepository campaignRepository,
        ProductRepository productRepository,
        StockRepository stockRepository, ThreadTaskScheduler scheduler) {
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.scheduler = scheduler;
    }

    @Transactional
    public CreateCampaignResponse createCampaign(
        CreateCampaignRequest campaignRequest,
        Member member,
        LocalDateTime now
    ) {
        CreateProductRequest productAtCampaignRequest = campaignRequest.productRequest();
        validateQuantity(productAtCampaignRequest.totalQuantity(), campaignRequest.goalQuantity());
        Stock stock = stockRepository.save(new Stock(productAtCampaignRequest.totalQuantity()));
        Campaign campaign = campaignRepository.save(new Campaign(
            campaignRequest.startDate(),
            campaignRequest.endDate(),
            campaignRequest.goalQuantity(),
            member,
            now
        ));
        productRepository.save(new Product(
            productAtCampaignRequest.name(),
            productAtCampaignRequest.description(),
            productAtCampaignRequest.price(),
            campaign,
            stock
        ));
        scheduler.scheduleCampaignToStart(campaign);
        scheduler.scheduleCampaignToFinish(campaign);
        return CreateCampaignResponse.from(campaign.getId());
    }

    private void validateQuantity(int totalQuantity, int goalQuantity) {
        if (totalQuantity <= goalQuantity) {
            throw new WiseShopException(WiseShopErrorCode.INVALID_GOAL_QUANTITY);
        }
    }

    public ReadCampaignResponse readCampaign(Long campaignId) {
        List<Product> findProducts = productRepository.findProductsByCampaignId(campaignId);
        if (findProducts.isEmpty()){
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_NOT_FOUND);
        }
        Product findProduct = findProducts.getFirst();
        Campaign findCampaign = findProduct.getCampaign();
        return new ReadCampaignResponse(
            campaignId,
            findCampaign.getStartDate().toString(),
            findCampaign.getEndDate().toString(), findCampaign.getGoalQuantity(),
            new ProductResponse(findProduct)
        );
    }

    public List<ReadCampaignResponse> readInProgressCampaign() {
        List<Campaign> campaigns = campaignRepository.findAllByState(CampaignState.IN_PROGRESS);
        return campaigns.stream()
            .map(campaign -> {
                Product product = productRepository.findAllByCampaign(campaign).getFirst();
                return ReadCampaignResponse.of(product, campaign);
            }).toList();
    }
}
