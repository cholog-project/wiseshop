package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyQuantityRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.JdbcProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final JdbcProductRepository productRepository;
    private final CampaignService campaignService;

    public ProductService(JdbcProductRepository productRepository, CampaignService campaignService) {
        this.productRepository = productRepository;
        this.campaignService = campaignService;
    }

    public Long createProduct(CreateProductRequest request) {
        Stock stock = new Stock(request.totalQuantity());
        Product product = new Product(request.name(), request.description(), request.price(), stock);
        Product createdProduct = productRepository.save(product);
        return createdProduct.getId();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        // TODO: Model 만드는 Repository로 바꿔주기
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        Campaign campaign = product.getCampaign();
        Member member = campaign.getMember();
        return new ProductResponse(product.toModel(member.toModel()));
    }

    public void modifyProduct(Long productId, ModifyProductRequest request) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.MODIFY_NAME_DESCRIPTION_PRODUCT_NOT_FOUND));
        existedProduct.modifyProduct(request.name(), request.description());
        productRepository.save(existedProduct);
    }

    public void modifyProductPrice(Long productId, ModifyProductPriceRequest request) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.MODIFY_PRICE_PRODUCT_NOT_FOUND));
        existedProduct.modifyPrice(request.price());
        productRepository.save(existedProduct);
    }

    //테스트 제외 사용되지 않는 메서드
    public void modifyStockQuantity(ModifyQuantityRequest request) {
        if (campaignService.isStarted(request.campaignId())) {
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_ALREADY_IN_PROGRESS);
        }
        Product existedProduct = productRepository.findById(request.productId())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        Stock existedStock = existedProduct.getStock();
        existedStock.modifyTotalQuantity(request.modifyQuantity());
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        productRepository.deleteById(id);
    }
}
