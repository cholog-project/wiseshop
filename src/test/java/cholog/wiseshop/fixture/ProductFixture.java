package cholog.wiseshop.fixture;

import cholog.wiseshop.db.product.Product;

@SuppressWarnings("NonAsciiCharacters")
public class ProductFixture {

    public static Product 보약() {
        return Product.builder()
            .name("보약")
            .description("먹으면 기분이 좋아져요.")
            .price(10000)
            .build();
    }
}
