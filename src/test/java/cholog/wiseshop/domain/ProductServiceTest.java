package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;

import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.fixture.ProductFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest extends BaseTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    class 상품_정보를_수정한다 {

        @Test
        void 상품_정보를_정상적으로_수정한다() {
            // given
            Product product = productRepository.save(ProductFixture.보약());
            ModifyProductRequest request = new ModifyProductRequest(
                "수정된 보약",
                "수정된 보약 설명"
            );
            // when
            productService.modifyProduct(product.getId(), request);

            // then
            Product modified = productRepository.findById(product.getId()).get();

            SoftAssertions.assertSoftly(
                softly -> {
                    assertThat(modified.getName()).isEqualTo("수정된 보약");
                    assertThat(modified.getDescription()).isEqualTo("수정된 보약 설명");
                }
            );
        }
    }
}
