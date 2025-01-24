package cholog.wiseshop.api.campaign.controller;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.AllCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.campaign.service.CampaignService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/campaigns")
    public ResponseEntity<Long> createCampaigns(@RequestBody CreateCampaignRequest request) {
        Long productId = campaignService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<ReadCampaignResponse> readCampaign(@PathVariable Long id) {
        ReadCampaignResponse response = campaignService.readCampaign(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<AllCampaignResponse>> readAllCampaign() {
        List<AllCampaignResponse> response = campaignService.readAllCampaign();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
