package cholog.wiseshop.fixture;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.stock.Stock;

@SuppressWarnings("NonAsciiCharacters")
public class ProductFixture {

    public static Product 보약(Member owner) {
        return Product.builder()
            .name("보약")
            .description("먹으면 기분이 좋아져요.")
            .price(10000)
            .owner(owner)
            .build();
    }

    public static Product 재고가_설정된_캠페인의_보약(Campaign campaign, Stock stock, Member owner) {
        return Product.builder()
            .campaign(campaign)
            .price(1000)
            .stock(stock)
            .owner(owner)
            .build();
    }

    public static Product 캠페인의_보약(Campaign campaign, Member owner) {
        return Product.builder()
            .campaign(campaign)
            .price(1000)
            .owner(owner)
            .build();
    }

    public static class Request {

        public static CreateProductRequest 보약_생성_요청() {
            return new CreateProductRequest(
                "보약",
                "먹으면 기분이 좋아져요.",
                10000,
                100
            );
        }
    }
}
