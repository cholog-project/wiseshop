package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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

    private final JobLauncher jobLauncher;
    private final Job orderCancellationJob;
    private final ProductRepository productRepository;


    public ThreadTaskScheduler(
        ThreadPoolTaskScheduler scheduler,
        PlatformTransactionManager transactionManager,
        CampaignRepository campaignRepository,
        ScheduledTaskStorage taskStorage,
        JobLauncher jobLauncher,
        Job orderCancellationJob,
        ProductRepository productRepository) {
        this.scheduler = scheduler;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.campaignRepository = campaignRepository;
        this.taskStorage = taskStorage;
        this.jobLauncher = jobLauncher;
        this.orderCancellationJob = orderCancellationJob;
        this.productRepository = productRepository;
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
        taskStorage.saveScheduleTask(scheduledTask, campaign);
    }

    public void scheduleCampaignToFinish(Campaign campaign) {
        Runnable endCampaign = () -> {
            transactionTemplate.execute(status -> {
                campaign.setStateWhenFinish();
                campaignRepository.save(campaign);
                return null;
            });
            if (campaign.isFailed()) {
                try {
                    Product product = productRepository.findAllByCampaignId(campaign.getId())
                        .getFirst();
                    JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("productId", product.getId())
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();
                    jobLauncher.run(orderCancellationJob, jobParameters);
                } catch (Exception e) {
                    throw new WiseShopException(WiseShopErrorCode.FAILED_ORDER_CANCEL,
                        e.getMessage());
                }
            }
        };
        ScheduledFuture<?> scheduledTask = scheduler.schedule(
            endCampaign,
            campaign.getEndDate().atZone(ZoneId.systemDefault()).toInstant()
        );
        taskStorage.saveScheduleTask(scheduledTask, campaign);

    }
}
