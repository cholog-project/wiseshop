package cholog.wiseshop.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    public void 상품과_재고_저장_조회하기() {
        // given
        CreateProductRequest request = getCreateProductRequest();

        // when
        Product savedProduct = productRepository.save(request.from());

        Product product = productRepository.findById(savedProduct.getId()).orElseThrow();

        // then
        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getDescription()).isEqualTo(request.description());
        assertThat(product.getPrice()).isEqualTo(request.price());
    }

    @Test
    public void 상품_이름_설명글_수정하기() {
        // given
        String modifiedName = "보약2";
        String modifiedDescription = "먹으면 기분이 안좋아져요.";

        CreateProductRequest request = getCreateProductRequest();

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
    public void 상품_가격_수정하기() {
        // given
        int modifiedPrice = 20000;

        CreateProductRequest request = getCreateProductRequest();

        // when
        Product createdProduct = productRepository.save(request.from());
        createdProduct.modifyPrice(modifiedPrice);
        productRepository.save(createdProduct);

        Product modifiedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        // then
        assertThat(modifiedProduct.getPrice()).isEqualTo(modifiedPrice);
    }

    @Test
    public void 상품_삭제하기() {
        // given
        final CreateProductRequest request = getCreateProductRequest();

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
