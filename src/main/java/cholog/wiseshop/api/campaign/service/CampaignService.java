package cholog.wiseshop.api.campaign.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ThreadPoolTaskScheduler scheduler;
    private final TransactionTemplate transactionTemplate;

    public CampaignService(CampaignRepository campaignRepository,
                           ProductRepository productRepository,
                           StockRepository stockRepository,
                           ThreadPoolTaskScheduler scheduler, PlatformTransactionManager transactionManager) {
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Long createCampaign(CreateCampaignRequest request) {
        CreateProductRequest productRequest = request.product();
        Stock stock = stockRepository.save(new Stock(productRequest.totalQuantity()));
        Product product = productRepository.save(new Product(
                productRequest.name(), productRequest.description(), productRequest.price(), stock));
        Campaign campaign = campaignRepository.save(
                new Campaign(request.startDate(), request.endDate(), request.goalQuantity()));
        product.addCampaign(campaign);
        scheduleCampaignDate(campaign.getId(), request.startDate(), request.endDate());
        return campaign.getId();
    }

    @Transactional(readOnly = true)
    public ReadCampaignResponse readCampaign(Long campaignId) {
        List<Product> findProducts = productRepository.findProductsByCampaignId(campaignId);
        if (findProducts.isEmpty()) {
            throw new IllegalArgumentException("캠페인이 존재하지 않습니다.");
        }
        Product findProduct = findProducts.get(0);
        Campaign findCampaign = findProduct.getCampaign();
        return new ReadCampaignResponse(
                campaignId,
                findCampaign.getStartDate().toString(),
                findCampaign.getEndDate().toString(), findCampaign.getGoalQuantity(),
                new ProductResponse(findProduct));
    }

    public void scheduleCampaignDate(Long campaignId, LocalDateTime startDate, LocalDateTime endDate) {
        Runnable startCampaign = () -> transactionTemplate.execute(status -> {
            changeCampaingState(campaignId, CampaignState.IN_PROGRESS);
            return null;
        });
        scheduler.schedule(startCampaign, startDate.atZone(ZoneId.systemDefault()).toInstant());

        Runnable endCampaign = () -> transactionTemplate.execute(status -> {
            changeCampaingState(campaignId, CampaignState.FAILED);
            return null;
        });
        scheduler.schedule(endCampaign, endDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void changeCampaingState(Long campaignId, CampaignState state) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("상태 변경할 캠페인 정보가 존재하지 않습니다."));
        campaign.updateState(state);
    }

    public boolean isStarted(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("캠페인이 존재하지 않습니다."));
        if (campaign.getState().equals(CampaignState.IN_PROGRESS)) {
            return true;
        }
        return false;
    }
}
