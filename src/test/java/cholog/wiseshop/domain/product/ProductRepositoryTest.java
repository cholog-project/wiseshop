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
    public void 상품_저장하기() {
        // given
        String name = "보약";
        String description = "먹으면 기분이 좋아져요.";
        int price = 10000;

        CreateProductRequest request = new CreateProductRequest(name, description, price);

        // when
        productRepository.save(request.from());

        Product product = productRepository.findById(1L).orElseThrow();

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getPrice()).isEqualTo(price);
    }

}
