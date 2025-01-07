package cholog.wiseshop.api.campaign.controller;

import cholog.wiseshop.api.campaign.dto.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
