package cholog.wiseshop.api.campaign.controller;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.dto.response.AllCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.CreateCampaignResponse;
import cholog.wiseshop.api.campaign.dto.response.ReadCampaignResponse;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/campaigns")
    public ResponseEntity<CreateCampaignResponse> createCampaigns(@Auth Member member,
        @RequestBody CreateCampaignRequest request) {
        var response = campaignService.createCampaign(request, member, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<ReadCampaignResponse> readCampaign(@PathVariable Long id) {
        ReadCampaignResponse response = campaignService.readCampaign(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<AllCampaignResponse> readAllCampaign() {
        List<ReadCampaignResponse> response = campaignService.readInProgressCampaign();
        return ResponseEntity.status(HttpStatus.OK).body(new AllCampaignResponse(response));
    }
}
