package cholog.wiseshop.api.campaign.service;

import cholog.wiseshop.api.campaign.domain.CampaignModel;
import cholog.wiseshop.api.campaign.domain.CampaignRepository;
import cholog.wiseshop.api.campaign.domain.CampaignStatus;
import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.product.domain.ProductModel;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.JdbcCampaignRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.JdbcProductRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final JdbcCampaignRepository jdbcCampaignRepository;
    private final JdbcProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ThreadPoolTaskScheduler scheduler;
    private final TransactionTemplate transactionTemplate;

    public CampaignService(
            CampaignRepository campaignRepository, JdbcCampaignRepository jdbcCampaignRepository,
            JdbcProductRepository productRepository,
            StockRepository stockRepository,
            ThreadPoolTaskScheduler scheduler,
            PlatformTransactionManager transactionManager
    ) {
        this.campaignRepository = campaignRepository;
        this.jdbcCampaignRepository = jdbcCampaignRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Long createCampaign(CreateCampaignRequest request, Member member) {
        CreateProductRequest productRequest = request.product();
        Stock stock = stockRepository.save(new Stock(productRequest.totalQuantity()));
        Product product = productRepository.save(
                new Product(productRequest.name(), productRequest.description(), productRequest.price(), stock));
        Campaign campaign = jdbcCampaignRepository.save(
                new Campaign(request.startDate(), request.endDate(), request.goalQuantity(), member));
        product.addCampaign(campaign);
        scheduleCampaignDate(campaign.getId(), request.startDate(), request.endDate());
        return campaign.getId();
    }

    @Transactional(readOnly = true)
    public ReadCampaignResponse readCampaign(Long campaignId) {
        CampaignModel campaign = campaignRepository.findById(campaignId);
        ProductModel product = campaign.products().getFirst();
        return new ReadCampaignResponse(
                campaignId,
                campaign.startDate().toString(),
                campaign.endDate().toString(),
                campaign.goalQuantity(),
                new ProductResponse(product)
        );
    }

    public void scheduleCampaignDate(
            Long campaignId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Runnable startCampaign = () -> transactionTemplate.execute(status -> {
            changeCampaignState(campaignId, CampaignStatus.IN_PROGRESS);
            return null;
        });
        scheduler.schedule(startCampaign, startDate.atZone(ZoneId.systemDefault()).toInstant());

        Runnable endCampaign = () -> transactionTemplate.execute(status -> {
            changeCampaignState(campaignId, CampaignStatus.FAILED);
            return null;
        });
        scheduler.schedule(endCampaign, endDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void changeCampaignState(Long campaignId, CampaignStatus state) {
        Campaign campaign = jdbcCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("상태 변경할 캠페인 정보가 존재하지 않습니다."));
        campaign.updateState(state);
    }

    public boolean isStarted(Long campaignId) {
        Campaign campaign = jdbcCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("캠페인이 존재하지 않습니다."));
        return CampaignStatus.IN_PROGRESS.equals(campaign.getState());
    }

    public List<ReadCampaignResponse> readAllCampaign() {
        List<CampaignModel> campaigns = campaignRepository.findAll();
        List<ReadCampaignResponse> allResponses = new ArrayList<>();
        for (var campaign : campaigns) {
            ReadCampaignResponse response = ReadCampaignResponse.of(
                    campaign.products().getFirst(),
                    campaign
            );
            allResponses.add(response);
        }
        return allResponses;
    }
}
