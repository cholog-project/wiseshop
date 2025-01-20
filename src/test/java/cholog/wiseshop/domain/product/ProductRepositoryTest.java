package cholog.wiseshop.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private StockRepository stockRepository;

    private static CreateProductRequest request;

    @BeforeEach
    public void cleanUp() {
        productRepository.deleteAll();
        request = getCreateProductRequest();
    }

    @Test
    void 상품_재고_캠페인_저장하기() {
        // given
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        Integer goalQuantity = 5;
        Campaign campaign = campaignRepository.save(new Campaign(startDate, endDate, goalQuantity));

        Product product = productRepository.save(request.from());
        product.addCampaign(campaign);

        // when
        List<Product> products = productRepository.findProductsByCampaignId(campaign.getId());

        // then
        assertAll(
                () -> assertThat(products).hasSize(1),
                () -> assertThat(products.get(0).getName()).isEqualTo(request.name()),
                () -> assertThat(products.get(0).getDescription()).isEqualTo(request.description()),
                () -> assertThat(products.get(0).getPrice()).isEqualTo(request.price()),
                () -> assertThat(products.get(0).getStock().getTotalQuantity()).isEqualTo(request.totalQuantity()),
                () -> assertThat(products.get(0).getCampaign().getId()).isEqualTo(campaign.getId())
        );
    }

    @Test
    void 상품_저장_조회하기() {
        // when
        Product savedProduct = productRepository.save(request.from());

        Product product = productRepository.findById(savedProduct.getId()).orElseThrow();

        // then
        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getDescription()).isEqualTo(request.description());
        assertThat(product.getPrice()).isEqualTo(request.price());
    }

    @Test
    void 상품_이름_설명글_수정하기() {
        // given
        String modifiedName = "보약2";
        String modifiedDescription = "먹으면 기분이 안좋아져요.";

        // when
        Product createdProduct = productRepository.save(request.from());
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

        // when
        Product createdProduct = productRepository.save(request.from());
        createdProduct.modifyPrice(modifiedPrice);
        productRepository.save(createdProduct);

        Product modifiedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        // then
        assertThat(modifiedProduct.getPrice()).isEqualTo(modifiedPrice);
    }

    @Test
    void 상품_삭제하기() {
        // when
        Product createdProduct = productRepository.save(request.from());

        productRepository.deleteById(createdProduct.getId());

        // then
        assertThat(productRepository.findById(createdProduct.getId())).isEmpty();
    }


    public static CreateProductRequest getCreateProductRequest() {
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        Integer price = 10000;
        Integer totalQuantity = 5;

        return new CreateProductRequest(name, description, price, totalQuantity);
    }
}
