package cholog.wiseshop.web.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
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

    @BeforeEach
    public void cleanUp() {
        productRepository.deleteAll();;
    }

    @Test
    public void 상품_저장_조회_성공() {
        // given
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;

        CreateProductRequest request = new CreateProductRequest(name, description, price);

        // when
        Long savedProductId = productService.createProduct(request);

        Product savedProduct = productRepository.findById(savedProductId)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // then
        assertThat(savedProduct.getName()).isEqualTo(name);
        assertThat(savedProduct.getDescription()).isEqualTo(description);
        assertThat(savedProduct.getPrice()).isEqualTo(price);
    }

    @Test
    public void 상품_조회_실패() {
        // given
        Long wrongProductId = 10L;

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> productService.getProduct(wrongProductId));

        // then
        assertThat(exception.getMessage()).isEqualTo("상품 조회에 실패했습니다.");
    }

    @Test
    public void 상품_이름_설명글_수정_성공() {
        // given
        String modifiedName = "보약2";
        String modifiedDescription = "먹으면 기분이 안좋아져요.";

        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;

        CreateProductRequest request = new CreateProductRequest(name, description, price);

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
    public void 상품_이름_설명글_수정_실패() {
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
    public void 상품_가격_수정_성공() {
        // given
        int modifiedPrice = 20000;

        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;

        CreateProductRequest request = new CreateProductRequest(name, description, price);

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
    public void 상품_가격_수정_실패() {
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
    public void 상품_삭제하기() {
        // given
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;

        CreateProductRequest request = new CreateProductRequest(name, description, price);

        // when
        Product createdProduct = productRepository.save(request.from());

        productService.deleteProduct(createdProduct.getId());

        // then
        assertThat(productRepository.findById(createdProduct.getId())).isEmpty();
    }

    @Test
    public void 상품_삭제_실패() {
        // given
        Long wrongProductId = 13L;

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> productService.deleteProduct(wrongProductId));

        // then
        assertThat(exception.getMessage()).isEqualTo("삭제할 상품이 존재하지 않습니다.");
    }
}
