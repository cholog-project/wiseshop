package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import java.time.ZoneId;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ThreadTaskScheduler {

    private final ThreadPoolTaskScheduler scheduler;
    private final TransactionTemplate transactionTemplate;

    public ThreadTaskScheduler(
        ThreadPoolTaskScheduler scheduler,
        PlatformTransactionManager transactionManager
    ) {
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void scheduleCampaignToStart(Campaign campaign) {
        Runnable startCampaign = () -> transactionTemplate.execute(status -> {
            campaign.setState(campaign.getStartDate(), campaign.getEndDate());
            return null;
        });
        scheduler.schedule(startCampaign, campaign.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
    }

    public void scheduleCampaignToFinish(Campaign campaign) {
        Runnable endCampaign = () -> transactionTemplate.execute(status -> {
            campaign.setStateWhenFinish();
            return null;
        });
        scheduler.schedule(endCampaign, campaign.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
    }
}
