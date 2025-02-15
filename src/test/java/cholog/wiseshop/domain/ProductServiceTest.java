package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.product.dto.request.ModifyProductPriceAndStockRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.CampaignFixture;
import cholog.wiseshop.fixture.MemberFixture;
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

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private StockRepository stockRepository;

    @Nested
    class 상품_정보를_수정한다 {

        @Test
        void 상품_정보를_정상적으로_수정한다() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Product product = productRepository.save(ProductFixture.보약(member));
            ModifyProductRequest request = new ModifyProductRequest(
                "수정된 보약",
                "수정된 보약 설명"
            );
            // when
            productService.modifyProduct(member, product.getId(), request);

            // then
            Product modified = productRepository.findById(product.getId()).get();

            SoftAssertions.assertSoftly(
                softly -> {
                    assertThat(modified.getName()).isEqualTo("수정된 보약");
                    assertThat(modified.getDescription()).isEqualTo("수정된 보약 설명");
                }
            );
        }

        @Test
        void 상품의_가격과_재고를_정상적으로_수정한다() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Campaign campaign = campaignRepository.save(CampaignFixture.대기중인_보약_캠페인(member));
            Stock stock = new Stock(20);
            stockRepository.save(stock);
            Product product = productRepository.save(
                ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, member)
            );
            ModifyProductPriceAndStockRequest request = new ModifyProductPriceAndStockRequest(
                2000,
                30
            );

            // when
            productService.modifyProductPriceAndStock(member, product.getId(), request);

            // then
            Product modified = productRepository.findById(product.getId()).get();
            assertThat(modified.getPrice()).isEqualTo(2000);
            assertThat(modified.getStock().getTotalQuantity()).isEqualTo(30);
        }

        @Test
        void 캠페인이_진행중일_경우_상품가격과_재고를_수정할_수_없다() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Campaign campaign = campaignRepository.save(CampaignFixture.진행중인_보약_캠페인(member));
            Stock stock = new Stock(20);
            stockRepository.save(stock);
            Product product = productRepository.save(
                ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, member)
            );
            ModifyProductPriceAndStockRequest request = new ModifyProductPriceAndStockRequest(
                2000,
                30
            );

            // when & then
            assertThatThrownBy(
                () -> productService.modifyProductPriceAndStock(member, product.getId(), request))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.CAMPAIGN_ALREADY_IN_PROGRESS.getMessage());
        }

        @Test
        void 재고를_목표수량보다_적게_수정할_수_없다() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Campaign campaign = campaignRepository.save(CampaignFixture.대기중인_보약_캠페인(member));
            Stock stock = new Stock(20);
            stockRepository.save(stock);
            Product product = productRepository.save(
                ProductFixture.재고가_설정된_캠페인의_보약(campaign, stock, member)
            );
            ModifyProductPriceAndStockRequest request = new ModifyProductPriceAndStockRequest(
                2000,
                1
            );

            // when & then
            assertThatThrownBy(
                () -> productService.modifyProductPriceAndStock(member, product.getId(), request))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.INVALID_TOTAL_QUANTITY.getMessage());
        }
    }

    @Nested
    class 상품을_삭제한다 {

        @Test
        void 상품_하나를_정상적으로_삭제한다() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Campaign campaign = campaignRepository.save(CampaignFixture.대기중인_보약_캠페인(member));
            Product product = productRepository.save(ProductFixture.캠페인의_보약(campaign, member));

            // when
            productService.deleteProduct(member, product.getId());

            //then
            assertThat(productRepository.findById(product.getId())).isEmpty();
            assertThat(campaignRepository.findById(campaign.getId())).isEmpty();
        }

        @Test
        void 캠페인이_진행중이면_상품을_삭제할_수_없다() {
            // given
            Member member = memberRepository.save(MemberFixture.최준호());
            Campaign campaign = campaignRepository.save(CampaignFixture.진행중인_보약_캠페인(member));
            Product product = productRepository.save(ProductFixture.캠페인의_보약(campaign, member));

            // when & then
            assertThatThrownBy(() -> productService.deleteProduct(member, product.getId()))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.INVALID_CAMPAIGN_DELETE_STATE.getMessage());
        }
    }
}
