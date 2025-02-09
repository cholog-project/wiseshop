package cholog.wiseshop.fixture;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.db.product.Product;

@SuppressWarnings("NonAsciiCharacters")
public class ProductFixture {

    public class Request {
        public static CreateProductRequest 보약_생성_요청() {
            return new CreateProductRequest(
                "보약",
                "먹으면 기분이 좋아져요.",
                10000,
                100
            );
        }
    }

    public static Product 보약() {
        return Product.builder()
            .name("보약")
            .description("먹으면 기분이 좋아져요.")
            .price(10000)
            .build();
    }
}
