package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.campaign.dto.request.CreateCampaignRequest.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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

    public void modifyProductPrice(Long productId, ModifyProductPriceRequest request) {
        Product existedProduct = productRepository.findById(productId)
            .orElseThrow(
                () -> new WiseShopException(WiseShopErrorCode.MODIFY_PRICE_PRODUCT_NOT_FOUND));
        existedProduct.modifyPrice(request.price());
        productRepository.save(existedProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        productRepository.deleteById(id);
    }
}
