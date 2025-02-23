package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
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
        taskStorage.saveToStart(scheduledTask, campaign);
    }

    public void scheduleCampaignToFinish(Campaign campaign) {
        Runnable endCampaign = () -> {
            // 1) 트랜잭션으로 캠페인 상태 업데이트
            transactionTemplate.execute(status -> {
                campaign.setStateWhenFinish();
                campaignRepository.save(campaign);
                return null; // 트랜잭션 내부 로직 종료
            });
            // 2) 캠페인 목표 달성 실패 여부 확인 후, 배치 실행
            if (campaign.isFailed()) { // 예: 목표수량 미달일 경우
                try {
                    Product product = productRepository.findAllByCampaignId(campaign.getId()).getFirst();
                    JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("productId", product.getId())
                        .addLong("time", System.currentTimeMillis()) // 재실행 위해 유니크 파라미터
                        .toJobParameters();
                    jobLauncher.run(orderCancellationJob, jobParameters);
                } catch (Exception e) {
                    // 예외 처리
                    e.printStackTrace();
                }
            }
        };
        // 스케줄링
        ScheduledFuture<?> scheduledTask = scheduler.schedule(
            endCampaign,
            campaign.getEndDate().atZone(ZoneId.systemDefault()).toInstant()
        );
        taskStorage.saveToStart(scheduledTask, campaign);

    }
}
