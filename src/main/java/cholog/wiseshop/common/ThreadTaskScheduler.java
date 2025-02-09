package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.time.ZoneId;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ThreadTaskScheduler {

    private final ThreadPoolTaskScheduler scheduler;
    private final TransactionTemplate transactionTemplate;
    private final CampaignRepository campaignRepository;

    public ThreadTaskScheduler(
        ThreadPoolTaskScheduler scheduler,
        PlatformTransactionManager transactionManager,
        CampaignRepository campaignRepository
    ) {
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.campaignRepository = campaignRepository;
    }

    public void scheduleCampaign(Campaign campaign) {
        Runnable startCampaign = () -> transactionTemplate.execute(status -> {
            changeCampaignState(campaign.getId(), CampaignState.IN_PROGRESS);
            return null;
        });
        scheduler.schedule(startCampaign, campaign.getStartDate().atZone(ZoneId.systemDefault()).toInstant());

        Runnable endCampaign = () -> transactionTemplate.execute(status -> {
            changeCampaignState(campaign.getId(), CampaignState.FAILED);
            return null;
        });
        scheduler.schedule(endCampaign, campaign.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
    }

    private void changeCampaignState(Long campaignId, CampaignState state) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.CAMPAIGN_NOT_FOUND));
        campaign.updateState(state);
    }
}
