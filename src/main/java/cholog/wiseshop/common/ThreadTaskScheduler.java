package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
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
        CampaignRepository campaignRepository) {
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.campaignRepository = campaignRepository;
    }

    public void scheduleCampaignToStart(Campaign campaign) {
        Runnable startCampaign = () -> transactionTemplate.execute(status -> {
            campaign.setState(campaign.getStartDate(), campaign.getEndDate());
            campaignRepository.save(campaign);
            return null;
        });
        scheduler.schedule(startCampaign, campaign.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
    }

    public void scheduleCampaignToFinish(Campaign campaign) {
        Runnable endCampaign = () -> transactionTemplate.execute(status -> {
            campaign.setStateWhenFinish();
            campaignRepository.save(campaign);
            return null;
        });
        scheduler.schedule(endCampaign, campaign.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
    }
}
