package cholog.wiseshop.web.campaign;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class CampaignControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private EntityManager entityManager;

    @LocalServerPort
    private int port;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        campaignRepository.deleteAll();
        productRepository.deleteAll();
    }

    @AfterEach
    public void cleanUp() {
        this.entityManager
                .createNativeQuery("ALTER TABLE product ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();

        this.entityManager
                .createNativeQuery("ALTER TABLE campaign ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
    }

    @Test
    void 캠페인_생성하기() throws Exception {
        // given
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;
        Product product = new Product(name, description, price);
        Product savedProduct = productRepository.save(product);

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;
        CreateCampaignRequest request = new CreateCampaignRequest(
                startDate, endDate, goalQuantity, savedProduct.getId());

        String url = "http://localhost:" + port + "/api/v1/campaigns";

        // when & then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"))
                .andDo(print());
    }

    @Test
    void 캠페인_조회하기() throws Exception {
        // given
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;
        Product product = new Product(name, description, price);
        Product savedProduct = productRepository.save(product);

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;
        CreateCampaignRequest request = new CreateCampaignRequest(
                startDate, endDate, goalQuantity, savedProduct.getId());
        Long campaignId = campaignService.createCampaign(request);

        String url = "http://localhost:" + port + "/api/v1/campaigns/" + campaignId;

        // when & then
        mockMvc.perform(get(url)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value(campaignId))
                .andExpect(jsonPath("$.productId").value(savedProduct.getId()))
                .andDo(print());
    }
}
