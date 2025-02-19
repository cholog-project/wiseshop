package cholog.wiseshop.api.testdata.controller;

import cholog.wiseshop.api.testdata.service.TestDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class TestDataController {

    private final TestDataService testDataService;

    public TestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping("/test/generate/test-member")
    public ResponseEntity<Void> generateTestMember() {
        testDataService.generateTestMember();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/generate/test-campaign")
    public ResponseEntity<Void> generateCampaign() {
        testDataService.generateTestData();
        return ResponseEntity.ok().build();
    }
}
