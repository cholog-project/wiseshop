package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceAndStockRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;

    public ProductService(ProductRepository productRepository, CampaignRepository campaignRepository) {
        this.productRepository = productRepository;
        this.campaignRepository = campaignRepository;
    }

    // TODO: 안쓰면 제거
    public Long createProduct(CreateProductRequest request) {
        Stock stock = new Stock(request.totalQuantity());
        Product product = Product.builder()
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .stock(stock)
            .build();
        Product createdProduct = productRepository.save(product);
        return createdProduct.getId();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return new ProductResponse(productRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND)));
    }

    public void modifyProduct(Long productId, ModifyProductRequest request) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new WiseShopException(
                WiseShopErrorCode.MODIFY_NAME_DESCRIPTION_PRODUCT_NOT_FOUND));
        existedProduct.modifyProduct(request.name(), request.description());
        productRepository.save(existedProduct);
    }

    public void modifyProductPriceAndStock(Long productId, ModifyProductPriceAndStockRequest request) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.MODIFY_PRICE_PRODUCT_NOT_FOUND));
        existedProduct.modifyPriceAndStock(request.price(), request.totalQuantity());
        productRepository.save(existedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        if (product.getCampaign().isNotWaiting()) {
            throw new WiseShopException(WiseShopErrorCode.INVALID_CAMPAIGN_DELETE_STATE);
        }
        Campaign campaign = product.getCampaign();
        productRepository.deleteById(id);
        if (productRepository.findAllByCampaign(campaign).isEmpty()) {
            campaignRepository.delete(campaign);
        }
    }
}
