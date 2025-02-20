package cholog.wiseshop.api.testdata.service;

import cholog.wiseshop.common.DatabaseCleaner;
import cholog.wiseshop.db.campaign.CampaignState;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TestDataService {

    private static final int ONE_HUNDRED_SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;

    private final DatabaseCleaner databaseCleaner;
    private final JdbcTemplate jdbcTemplate;

    public TestDataService(
        DatabaseCleaner databaseCleaner,
        JdbcTemplate jdbcTemplate
    ) {
        this.databaseCleaner = databaseCleaner;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void generateTestMember() {
        cleanAllData();
        generateTestMemberData(ONE_HUNDRED_SIZE);
    }

    public void generateTestCampaign() {
        cleanAllData();
        generateTestMemberData(2);
        generateTestCampaignData(ONE_HUNDRED_SIZE);
        generateTestStockData(ONE_HUNDRED_SIZE);
        generateTestProductData(ONE_HUNDRED_SIZE);
    }

    public void generateTestOrder() {
        cleanAllData();
        generateTestMemberData(ONE_HUNDRED_SIZE + 1);
        generateTestCampaignData(1);
        generateTestStockData(ONE_HUNDRED_SIZE);
        generateTestProductData(ONE_HUNDRED_SIZE);
        generateTestOrderData(ONE_HUNDRED_SIZE);
    }

    public void generateTestMemberData(int size) {
        String sql = "INSERT INTO member (email, name, password) VALUES (?, ?, ?)";
        List<Object[]> memberBatch = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            String email = "user" + i + "@example.com";
            String name = "user-" + UUID.randomUUID().toString().substring(0, 10);
            String password = "$2a$10$C5.NxKqjo2FC72RjSWJj1uNtCbia5ClEY5KhMtO7jEUN6N5s3.ZVu";

            memberBatch.add(new Object[]{email, name, password});

            if (memberBatch.size() % BATCH_SIZE == 0) {
                batchTestMember(sql, memberBatch);
            }
        }

        if (!memberBatch.isEmpty()) {
            batchTestMember(sql, memberBatch);
        }
    }

    public void generateTestCampaignData(int size) {
        String sql = "INSERT INTO "
            + "campaign (start_date, end_date, goal_quantity, sold_quantity, state, member_id) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
        List<Object[]> campaignBatch = new ArrayList<>();
        Long memberId = getTestDataIds("member", 1).getFirst();

        for (int i = 1; i <= size; i++) {
            LocalDateTime now = LocalDateTime.now();

            LocalDateTime startDate = now.minusDays(1);
            LocalDateTime endDate = now.plusMinutes(10);
            int goalQuantity = 10;
            int soldQuantity = 0;
            String state = CampaignState.IN_PROGRESS.toString();

            campaignBatch.add(new Object[]{
                startDate,
                endDate,
                goalQuantity,
                soldQuantity,
                state,
                memberId
            });

            if (campaignBatch.size() % BATCH_SIZE == 0) {
                batchTestMember(sql, campaignBatch);
            }
        }

        if (!campaignBatch.isEmpty()) {
            batchTestMember(sql, campaignBatch);
        }
    }

    public void generateTestStockData(int size) {
        String sql = "INSERT INTO stock (total_quantity) VALUES (?)";
        List<Object[]> stockBatch = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            int totalQuantity = 100;

            stockBatch.add(new Object[]{totalQuantity});

            if (stockBatch.size() % BATCH_SIZE == 0) {
                batchTestMember(sql, stockBatch);
            }
        }

        if (!stockBatch.isEmpty()) {
            batchTestMember(sql, stockBatch);
        }
    }

    public void generateTestProductData(int size) {
        String sql = "INSERT INTO "
            + "product (name, description, price, campaign_id, stock_id, member_id) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
        List<Object[]> productBatch = new ArrayList<>();
        Long campaignId = getTestDataIds("campaign", 1).getFirst();
        List<Long> stockIds = getTestDataIds("stock", size);
        Long memberId = getTestDataIds("member", 1).getFirst();

        for (int i = 1; i <= size; i++) {
            String name = "Test Product-" + UUID.randomUUID().toString().substring(0, 5);
            String description = "Test Description" + UUID.randomUUID().toString().substring(0, 10);
            int price = (int) (Math.random() * 100) + 10000;
            Long stockId = stockIds.get(i - 1);

            productBatch.add(new Object[]{
                name,
                description,
                price,
                campaignId,
                stockId,
                memberId
            });

            if (productBatch.size() % BATCH_SIZE == 0) {
                batchTestMember(sql, productBatch);
            }
        }

        if (!productBatch.isEmpty()) {
            batchTestMember(sql, productBatch);
        }
    }

    public void generateTestOrderData(int size) {
        String sql = "INSERT INTO "
            + "`order` (count, product_id, member_id, address, created_date, modified_date) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
        List<Object[]> orderBatch = new ArrayList<>();
        List<Long> productIds = getTestDataIds("product", size);
        List<Long> memberIds = getTestDataIds("member", size + 1);

        for (int i = 1; i <= size; i++) {
            int count = 1;
            Long productId = productIds.get(i - 1);
            Long memberId = memberIds.get(i);
            LocalDateTime createdDate = LocalDateTime.now();
            LocalDateTime modifiedDate = LocalDateTime.now();
            String address = "테스트 집-" + UUID.randomUUID().toString().substring(0, 10);

            orderBatch.add(new Object[]{
                count,
                productId,
                memberId,
                address,
                createdDate,
                modifiedDate
            });

            if (orderBatch.size() % BATCH_SIZE == 0) {
                batchTestMember(sql, orderBatch);
            }
        }

        if (!orderBatch.isEmpty()) {
            batchTestMember(sql, orderBatch);
        }
    }

    public void batchTestMember(String sql, List<Object[]> batchArgs) {
        jdbcTemplate.batchUpdate(sql, batchArgs);
        batchArgs.clear();
    }

    public List<Long> getTestDataIds(String tableName, int limitLength) {
        return jdbcTemplate.queryForList(
            "SELECT id FROM "
                + tableName
                + " LIMIT "
                + limitLength,
            Long.class
        );
    }

    public void cleanAllData() {
        databaseCleaner.clear();
    }
}
