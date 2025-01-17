package cholog.wiseshop.web.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
public class ProductControllerTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    private static Product product;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        product = makeTestProduct();
        productRepository.save(product);
    }

    private static Product makeTestProduct() {
        String name = "보약2";
        String description = "먹으면 기분이 좋아지지 않아요.";
        int price = 50000;
        Integer totalQuantity = 5;

        Stock stock = new Stock(totalQuantity);
        return new Product(name, description, price, stock);
    }

    @AfterEach
    public void cleanUp() {
        this.entityManager
                .createNativeQuery("ALTER TABLE product ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
    }

    @Test
    public void 상품_조회하기() throws Exception {
        // when
        String getUrl = "http://localhost:" + port + "/api/v1/products/" + product.getId();

        //then
        mockMvc.perform(get(getUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andDo(print());
    }

    @Test
    public void 상품_이름_설명글_수정하기() throws Exception {
        // given
        String modifiedName = "보약3";
        String modifiedDescription = "먹으면 기분이 그냥그래요.";

        // when
        ModifyProductRequest request =
                new ModifyProductRequest(product.getId(), modifiedName, modifiedDescription);

        String url = "http://localhost:" + port + "/api/v1/products";

        // then
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void 상품_가격_수정하기() throws Exception {
        // given
        int modifiedPrice = 30000;

        // when
        ModifyProductPriceRequest request = new ModifyProductPriceRequest(product.getId(), modifiedPrice);

        String url = "http://localhost:" + port + "/api/v1/products/price";

        // then
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void 상품_삭제하기() throws Exception {
        // when
        String url = "http://localhost:" + port + "/api/v1/products/" + product.getId();

        // then
        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
