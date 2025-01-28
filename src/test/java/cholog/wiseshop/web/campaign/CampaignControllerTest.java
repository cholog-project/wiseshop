package cholog.wiseshop.web.campaign;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.db.campaign.CampaignRepository;
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
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        Integer goalQuantity = 5;

        CreateProductRequest productRequest = getCreateProductRequest();
        CreateCampaignRequest request = new CreateCampaignRequest(startDate, endDate, goalQuantity,
            productRequest);

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
    void 캠페인_단건_조회하기() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        CreateProductRequest productRequest = getCreateProductRequest();
        CreateCampaignRequest request = new CreateCampaignRequest(startDate, endDate, goalQuantity,
            productRequest);

        String postUrl = "http://localhost:" + port + "/api/v1/campaigns";
        mockMvc.perform(post(postUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().string("1"))
            .andDo(print());
        String getUrl = "http://localhost:" + port + "/api/v1/campaigns/" + 1;

        // when & then
        mockMvc.perform(get(getUrl)
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.campaignId").value(1))
            .andExpect(jsonPath("$.startDate").value(startDate.toString()))
            .andExpect(jsonPath("$.endDate").value(endDate.toString()))
            .andExpect(jsonPath("$.goalQuantity").value(goalQuantity))
            .andDo(print());
    }

    @Test
    void 캠페인_전체_조회하기() throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30);
        int goalQuantity = 5;

        CreateProductRequest productRequest = getCreateProductRequest();
        CreateCampaignRequest request = new CreateCampaignRequest(startDate, endDate, goalQuantity,
            productRequest);

        String postUrl = "http://localhost:" + port + "/api/v1/campaigns";
        mockMvc.perform(post(postUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(new ObjectMapper().writeValueAsString(request)));
        mockMvc.perform(post(postUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(new ObjectMapper().writeValueAsString(request)));

        String getUrl = "http://localhost:" + port + "/api/v1/campaigns";

        // when & then
        mockMvc.perform(get(getUrl)
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andDo(print());
    }
}
