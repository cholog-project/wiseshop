package cholog.wiseshop.api.campaign.domain;

import java.util.List;

public interface CampaignRepository {
    CampaignModel findById(Long id);

    List<CampaignModel> findAll();
}
