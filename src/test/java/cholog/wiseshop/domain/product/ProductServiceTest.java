package cholog.wiseshop.domain.product;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest;
import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyQuantityRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeEach
    void cleanUp() {
        campaignRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void 상품과_재고_저장_성공() {
        // given
        CreateProductRequest request = getCreateProductRequest();

        // when
        Long productId = productService.createProduct(request);
        Product findProduct = productRepository.findById(productId).orElseThrow();

        // then
        assertThat(findProduct.getName()).isEqualTo(request.name());
        assertThat(findProduct.getPrice()).isEqualTo(request.price());
        assertThat(findProduct.getDescription()).isEqualTo(request.description());
        assertThat(findProduct.getStock().getTotalQuantity()).isEqualTo(request.totalQuantity());
    }

    @Test
    void 상품과_재고_조회_성공() {
        // given
        CreateProductRequest request = getCreateProductRequest();

        // when
        Long productId = productService.createProduct(request);
        ProductResponse response = productService.getProduct(productId);

        // then
        assertThat(response.name()).isEqualTo(request.name());
        assertThat(response.price()).isEqualTo(request.price());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.totalQuantity()).isEqualTo(request.totalQuantity());
    }

    @Test
    void 상품_조회_실패() {
        // given
        Long wrongProductId = 10L;

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> productService.getProduct(wrongProductId));

        // then
        assertThat(exception.getMessage()).isEqualTo("상품 조회에 실패했습니다.");
    }

    @Test
    void 상품_이름_설명글_수정_성공() {
        // given
        String modifiedName = "보약2";
        String modifiedDescription = "먹으면 기분이 안좋아져요.";

        CreateProductRequest request = getCreateProductRequest();

        // when
        Product createdProduct = productRepository.save(request.from());

        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                createdProduct.getId(),
                modifiedName,
                modifiedDescription
        );

        productService.modifyProduct(modifyProductRequest);

        Product modifiedProduct = productRepository.findById(createdProduct.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // then
        assertThat(modifiedProduct.getName()).isEqualTo(modifiedName);
        assertThat(modifiedProduct.getDescription()).isEqualTo(modifiedDescription);
    }

    @Test
    void 상품_이름_설명글_수정_실패() {
        // given
        Long productId = 11L;
        String modifiedName = "보약2";
        String modifiedDescription = "먹으면 기분이 안좋아져요.";

        ModifyProductRequest request = new ModifyProductRequest(productId, modifiedName, modifiedDescription);

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> productService.modifyProduct(request));

        // then
        assertThat(exception.getMessage()).isEqualTo("이름 및 설명글 수정할 상품이 존재하지 않습니다.");
    }

    @Test
    void 상품_가격_수정_성공() {
        // given
        int modifiedPrice = 20000;

        CreateProductRequest request = getCreateProductRequest();

        // when
        Product createdProduct = productRepository.save(request.from());

        ModifyProductPriceRequest modifyProductPriceRequest = new ModifyProductPriceRequest(
                createdProduct.getId(),
                modifiedPrice
        );

        productService.modifyProductPrice(modifyProductPriceRequest);

        Product modifiedProduct = productRepository.findById(createdProduct.getId())
                .orElseThrow(() -> new IllegalArgumentException("가격 수정할 상품이 존재하지 않습니다."));

        // then
        assertThat(modifiedProduct.getPrice()).isEqualTo(modifiedPrice);
    }

    @Test
    void 상품_가격_수정_실패() {
        // given
        Long productId = 1L;
        int modifiedPrice = 30000;

        ModifyProductPriceRequest request = new ModifyProductPriceRequest(productId, modifiedPrice);

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> productService.modifyProductPrice(request));

        // then
        assertThat(exception.getMessage()).isEqualTo("가격 수정할 상품이 존재하지 않습니다.");
    }

    @Test
    void 상품_재고_수량_수정_성공() {
        // given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30, 10);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30, 10);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Integer modifyQuantity = 1;

        // when
        ModifyQuantityRequest modifyQuantityRequest = new ModifyQuantityRequest(
                campaignId,
                productId,
                modifyQuantity
        );
        productService.modifyStockQuantity(modifyQuantityRequest);

        Product modifiedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        Stock modifiedStock = modifiedProduct.getStock();

        // then
        assertThat(modifiedStock.getTotalQuantity()).isEqualTo(modifyQuantity);
    }

    @Test
    void 상품_재고_수량_수정_실패() {
        // given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);

        LocalDateTime startDate = LocalDateTime.of(2025, 1, 7, 10, 30, 10);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 8, 10, 30, 10);
        int goalQuantity = 5;

        Long campaignId = campaignService.createCampaign(
                new CreateCampaignRequest(startDate, endDate, goalQuantity, productId));
        Integer modifyQuantity = 0;

        // when
        ModifyQuantityRequest modifyQuantityRequest = new ModifyQuantityRequest(
                campaignId,
                productId,
                modifyQuantity
        );

        // then
        assertThatThrownBy(() -> productService.modifyStockQuantity(modifyQuantityRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 상품과_재고_삭제하기() {
        // given
        CreateProductRequest request = getCreateProductRequest();
        Long productId = productService.createProduct(request);
        Product createdProduct = productRepository.findById(productId).orElseThrow();
        Stock createdStock = createdProduct.getStock();

        // when
        productService.deleteProduct(productId);

        // then
        assertThat(productRepository.findById(productId)).isEmpty();
        assertThat(stockRepository.findById(createdStock.getId())).isEmpty();
    }

    @Test
    void 상품_삭제_실패() {
        // given
        Long wrongProductId = 13L;

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> productService.deleteProduct(wrongProductId));

        // then
        assertThat(exception.getMessage()).isEqualTo("삭제할 상품이 존재하지 않습니다.");
    }
}
