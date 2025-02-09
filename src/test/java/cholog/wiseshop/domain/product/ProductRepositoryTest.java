package cholog.wiseshop.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProductRepositoryTest extends BaseTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private StockRepository stockRepository;

    private static CreateProductRequest request;

    @AfterEach
    public void cleanUp() {
        productRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
        campaignRepository.deleteAllInBatch();
        request = getCreateProductRequest();
    }

    @Test
    void 상품_재고_캠페인_저장하기() {
        // given
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        int goalQuantity = 5;
        int totalQuantity = 10;
        int price = 2000;
        Stock stock = new Stock(totalQuantity);
        stockRepository.save(stock);
        Campaign campaign = campaignRepository.save(new Campaign(startDate, endDate, goalQuantity));
        productRepository.save(new Product("name", "description", price, campaign, stock));

        // when
        List<Product> products = productRepository.findProductsByCampaignId(campaign.getId());
        System.out.println("products.size() = " + products.size());

        // then
        assertAll(
            () -> assertThat(products).hasSize(1),
            () -> assertThat(products.getFirst().getName()).isEqualTo("name"),
            () -> assertThat(products.getFirst().getDescription()).isEqualTo("description"),
            () -> assertThat(products.getFirst().getPrice()).isEqualTo(price),
            () -> assertThat(products.getFirst().getStock().getTotalQuantity()).isEqualTo(totalQuantity),
            () -> assertThat(products.getFirst().getCampaign().getId()).isEqualTo(campaign.getId())
        );
    }

    @Test
    void 상품_저장_조회하기() {
        // given
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        int goalQuantity = 5;
        int totalQuantity = 10;
        int price = 2000;
        Stock stock = new Stock(totalQuantity);
        stockRepository.save(stock);
        Campaign campaign = campaignRepository.save(new Campaign(startDate, endDate, goalQuantity));
        Product savedProduct = productRepository.save(new Product("name", "description", price, campaign, stock));

        // when
        Product product = productRepository.findById(savedProduct.getId()).orElseThrow();

        // then
        assertThat(product.getName()).isEqualTo("name");
        assertThat(product.getDescription()).isEqualTo("description");
        assertThat(product.getPrice()).isEqualTo(price);
    }

    @Test
    void 상품_이름_설명글_수정하기() {
        // given
        String modifiedName = "보약2";
        String modifiedDescription = "먹으면 기분이 안좋아져요.";
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        int goalQuantity = 5;
        int totalQuantity = 10;
        int price = 2000;
        Stock stock = new Stock(totalQuantity);
        stockRepository.save(stock);
        Campaign campaign = campaignRepository.save(new Campaign(startDate, endDate, goalQuantity));
        Product createdProduct = productRepository.save(new Product("name", "description", price, campaign, stock));

        // when
        createdProduct.modifyProduct(modifiedName, modifiedDescription);
        productRepository.save(createdProduct);

        Product modifiedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        // then
        assertThat(modifiedProduct.getName()).isEqualTo(modifiedName);
        assertThat(modifiedProduct.getDescription()).isEqualTo(modifiedDescription);
    }

    @Test
    void 상품_가격_수정하기() {
        // given
        int modifiedPrice = 20000;
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        int goalQuantity = 5;
        int totalQuantity = 10;
        int price = 2000;
        Stock stock = new Stock(totalQuantity);
        stockRepository.save(stock);
        Campaign campaign = campaignRepository.save(new Campaign(startDate, endDate, goalQuantity));
        Product createdProduct = productRepository.save(new Product("name", "description", price, campaign, stock));

        // when
        createdProduct.modifyPrice(modifiedPrice);
        productRepository.save(createdProduct);

        Product modifiedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        // then
        assertThat(modifiedProduct.getPrice()).isEqualTo(modifiedPrice);
    }

    @Test
    void 상품_삭제하기() {
        // given
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        int goalQuantity = 5;
        int totalQuantity = 10;
        int price = 2000;
        Stock stock = new Stock(totalQuantity);
        stockRepository.save(stock);
        Campaign campaign = campaignRepository.save(new Campaign(startDate, endDate, goalQuantity));
        Product createdProduct = productRepository.save(new Product("name", "description", price, campaign, stock));

        // when
        productRepository.deleteById(createdProduct.getId());

        // then
        assertThat(productRepository.findById(createdProduct.getId())).isEmpty();
    }

    public static CreateProductRequest getCreateProductRequest() {
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;
        int totalQuantity = 5;

        return new CreateProductRequest(name, description, price, totalQuantity);
    }
}
