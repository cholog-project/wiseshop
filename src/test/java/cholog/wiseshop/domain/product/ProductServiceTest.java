package cholog.wiseshop.domain.product;

import static cholog.wiseshop.domain.product.ProductRepositoryTest.getCreateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProductServiceTest extends BaseTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CampaignService campaignService;

    @BeforeEach
    void cleanUp() {
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

        // when, then
        assertThatThrownBy(() -> productService.getProduct(wrongProductId))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.PRODUCT_NOT_FOUND.getMessage());
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
            modifiedName,
            modifiedDescription
        );

        productService.modifyProduct(createdProduct.getId(), modifyProductRequest);

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

        ModifyProductRequest request = new ModifyProductRequest(modifiedName, modifiedDescription);

        // when, then
        assertThatThrownBy(() -> productService.modifyProduct(productId, request))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.MODIFY_NAME_DESCRIPTION_PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    void 상품_가격_수정_성공() {
        // given
        int modifiedPrice = 20000;

        CreateProductRequest request = getCreateProductRequest();

        // when
        Product createdProduct = productRepository.save(request.from());

        ModifyProductPriceRequest modifyProductPriceRequest = new ModifyProductPriceRequest(
            modifiedPrice);

        productService.modifyProductPrice(createdProduct.getId(), modifyProductPriceRequest);

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
        ModifyProductPriceRequest request = new ModifyProductPriceRequest(modifiedPrice);

        // when, then
        assertThatThrownBy(() -> productService.modifyProductPrice(productId, request))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.MODIFY_PRICE_PRODUCT_NOT_FOUND.getMessage());
    }

    // TODO: 상품 재고 수량 수정 성공 테스트 작성
    // TODO: 상품 재고 수량 수정 실패 테스트 작성
    // TODO: 상품 재고 삭제 테스트 작성

    @Test
    void 상품_삭제_실패() {
        // given
        Long wrongProductId = 13L;

        // when, then
        assertThatThrownBy(() -> productService.deleteProduct(wrongProductId))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }
}
