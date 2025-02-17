package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.product.dto.request.ModifyProductPriceAndStockRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.common.ScheduledTaskStorage;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;
    private final ScheduledTaskStorage scheduledTaskStorage;

    public ProductService(ProductRepository productRepository,
        CampaignRepository campaignRepository, ScheduledTaskStorage scheduledTaskStorage) {
        this.productRepository = productRepository;
        this.campaignRepository = campaignRepository;
        this.scheduledTaskStorage = scheduledTaskStorage;
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return new ProductResponse(productRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND)));
    }

    public void modifyProduct(
        Member member,
        Long productId,
        ModifyProductRequest request
    ) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new WiseShopException(
                WiseShopErrorCode.MODIFY_NAME_DESCRIPTION_PRODUCT_NOT_FOUND));
        if (!existedProduct.isOwner(member)) {
            throw new WiseShopException(WiseShopErrorCode.NOT_OWNER);
        }
        existedProduct.modifyProduct(request.name(), request.description());
        productRepository.save(existedProduct);
    }

    public void modifyProductPriceAndStock(
        Member member,
        Long productId,
        ModifyProductPriceAndStockRequest request
    ) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(
                () -> new WiseShopException(WiseShopErrorCode.MODIFY_PRICE_PRODUCT_NOT_FOUND));
        if (!existedProduct.isOwner(member)) {
            throw new WiseShopException(WiseShopErrorCode.NOT_OWNER);
        }
        existedProduct.modifyPriceAndStock(request.price(), request.totalQuantity());
        productRepository.save(existedProduct);
    }

    public void deleteProduct(Member member, Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        if (!product.isOwner(member)) {
            throw new WiseShopException(WiseShopErrorCode.NOT_OWNER);
        }
        if (product.getCampaign().isNotWaiting()) {
            throw new WiseShopException(WiseShopErrorCode.INVALID_CAMPAIGN_DELETE_STATE);
        }
        Campaign campaign = product.getCampaign();
        productRepository.deleteById(id);
        if (productRepository.findAllByCampaign(campaign).isEmpty()) {
            scheduledTaskStorage.deleteAll(campaign);
            campaignRepository.delete(campaign);
        }
    }
}
