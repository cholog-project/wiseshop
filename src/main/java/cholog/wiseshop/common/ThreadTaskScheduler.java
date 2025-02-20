package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ThreadTaskScheduler {

    private final ThreadPoolTaskScheduler scheduler;
    private final TransactionTemplate transactionTemplate;
    private final CampaignRepository campaignRepository;
    private final ScheduledTaskStorage taskStorage;

    public ThreadTaskScheduler(
        ThreadPoolTaskScheduler scheduler,
        PlatformTransactionManager transactionManager,
        CampaignRepository campaignRepository, ScheduledTaskStorage taskStorage
    ) {
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.campaignRepository = campaignRepository;
        this.taskStorage = taskStorage;
    }

    public void scheduleCampaignToStart(Campaign campaign) {
        Runnable startCampaign = () -> transactionTemplate.execute(status -> {
            campaign.setState(campaign.getStartDate(), campaign.getEndDate());
            campaignRepository.save(campaign);
            scheduleCampaignToFinish(campaign);
            return null;
        });
        ScheduledFuture<?> scheduledTask = scheduler.schedule(
            startCampaign, campaign.getStartDate().atZone(ZoneId.systemDefault()).toInstant()
        );
        taskStorage.saveToStart(scheduledTask, campaign);
    }

    public void scheduleCampaignToFinish(Campaign campaign) {
        Runnable endCampaign = () -> transactionTemplate.execute(status -> {
            campaign.setStateWhenFinish();
            campaignRepository.save(campaign);
            return null;
        });
        ScheduledFuture<?> scheduledTask = scheduler.schedule(
            endCampaign, campaign.getEndDate().atZone(ZoneId.systemDefault()).toInstant()
        );
        taskStorage.saveToStart(scheduledTask, campaign);
    }
}
