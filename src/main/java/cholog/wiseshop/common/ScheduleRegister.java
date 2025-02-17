package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ScheduleRegister {

    private final CampaignRepository campaignRepository;
    private final ThreadTaskScheduler scheduler;

    public ScheduleRegister(CampaignRepository campaignRepository, ThreadTaskScheduler scheduler) {
        this.campaignRepository = campaignRepository;
        this.scheduler = scheduler;
    }

    @PostConstruct
    private void postConstructCampaigns() {
        List<Campaign> campaigns = campaignRepository.findAllByStates(
            List.of(CampaignState.WAITING, CampaignState.IN_PROGRESS)
        );
        for (Campaign campaign : campaigns) {
            if (campaign.getState().equals(CampaignState.WAITING)) {
                scheduler.scheduleCampaignToStart(campaign);
            } else {
                scheduler.scheduleCampaignToFinish(campaign);
            }
        }
    }
}
