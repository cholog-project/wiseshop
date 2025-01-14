package cholog.wiseshop.api.campaign.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;
    private final ThreadPoolTaskScheduler scheduler;

    public CampaignService(CampaignRepository campaignRepository,
                           ProductRepository productRepository,
                           ThreadPoolTaskScheduler scheduler) {
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.scheduler = scheduler;
    }

    public Long createCampaign(CreateCampaignRequest request) {
        Product findProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Campaign savedCampaign = campaignRepository.save(
                new Campaign(findProduct, request.startDate(), request.endDate(), request.goalQuantity()));
        scheduleCampaignDate(request.productId(), request.startDate(), request.endDate());
        return savedCampaign.getId();
    }

    @Transactional(readOnly = true)
    public ReadCampaignResponse readCampaign(Long id) {
        Campaign findCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("캠페인이 존재하지 않습니다."));
        return new ReadCampaignResponse(findCampaign.getId(), findCampaign.getProduct().getId());
    }

    public void scheduleCampaignDate(Long campaignId, LocalDateTime startDate, LocalDateTime endDate) {
        Runnable startCampaign = () -> changeCampaingState(campaignId, CampaignState.IN_PROGRESS);
        scheduler.schedule(startCampaign, startDate.atZone(ZoneId.systemDefault()).toInstant());
        scheduler.getScheduledExecutor();

        Runnable endCampaign = () -> changeCampaingState(campaignId, CampaignState.FAILED);
        scheduler.schedule(endCampaign, endDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void changeCampaingState(Long campaignId, CampaignState state) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("상태 변경할 캠페인 정보가 존재하지 않습니다."));
        campaign.updateState(state);
        campaignRepository.saveAndFlush(campaign);
    }
}
