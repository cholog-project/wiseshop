package cholog.wiseshop.api.campaign.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.CreateCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.common.ThreadTaskScheduler;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.ArrayList;
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
    public CreateCampaignResponse createCampaign(CreateCampaignRequest request, Member member) {
        Campaign campaign = saveStockAndCampaignAndProduct(request, member);
        scheduler.scheduleCampaignByDate(campaign);
        return CreateCampaignResponse.from(campaign.getId());
    }

    private Campaign saveStockAndCampaignAndProduct(CreateCampaignRequest campaignRequest, Member member) {
        CreateProductRequest productAtCampaignRequest = campaignRequest.product();
        Stock stock = stockRepository.save(new Stock(productAtCampaignRequest.totalQuantity()));
        Campaign campaign = campaignRepository.save(new Campaign(
            campaignRequest.startDate(),
            campaignRequest.endDate(),
            campaignRequest.goalQuantity(),
            member
        ));
        productRepository.save(new Product(
            productAtCampaignRequest.name(),
            productAtCampaignRequest.description(),
            productAtCampaignRequest.price(),
            campaign,
            stock
        ));
        return campaign;
    }

    public ReadCampaignResponse readCampaign(Long campaignId) {
        List<Product> findProducts = productRepository.findProductsByCampaignId(campaignId);
        if (findProducts.isEmpty()) {
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
//
//    public boolean isStarted(Long campaignId) {
//        Campaign campaign = campaignRepository.findById(campaignId)
//            .orElseThrow(() -> new IllegalArgumentException("캠페인이 존재하지 않습니다."));
//        if (campaign.getState().equals(CampaignState.IN_PROGRESS)) {
//            return true;
//        }
//        return false;
//    }

    public List<ReadCampaignResponse> readAllCampaign() {
        List<Product> products = productRepository.findAll();
        List<ReadCampaignResponse> allResponses = new ArrayList<>();
        for (Product product : products) {
            ReadCampaignResponse response = ReadCampaignResponse.of(
                product,
                product.getCampaign());
            allResponses.add(response);
        }
        return allResponses;
    }
}
