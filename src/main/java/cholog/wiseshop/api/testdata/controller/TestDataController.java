package cholog.wiseshop.api.testdata.controller;

import cholog.wiseshop.api.testdata.service.TestDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/test/generate")
@RestController
public class TestDataController {

    private final TestDataService testDataService;

    public TestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping("/test-member")
    public ResponseEntity<Void> generateTestMember() {
        testDataService.generateTestMember();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/generate/test-campaign")
    public ResponseEntity<Void> generateCampaign() {
        testDataService.generateTestCampaign();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test-order")
    public ResponseEntity<Void> generateOrder() {
        testDataService.generateTestOrder();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/test-delete")
    public ResponseEntity<Void> deleteTestData() {
        testDataService.truncateAllData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
