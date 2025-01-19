package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.campaign.service.CampaignService;
import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyQuantityRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CampaignService campaignService;

    public ProductService(ProductRepository productRepository, CampaignService campaignService) {
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
        return new ProductResponse(productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 조회에 실패했습니다.")));
    }

    public void modifyProduct(Long productId, ModifyProductRequest request) {
        Product existedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("이름 및 설명글 수정할 상품이 존재하지 않습니다."));
        existedProduct.modifyProduct(request.name(), request.description());
        productRepository.save(existedProduct);
    }

    public void modifyProductPrice(Long productId, ModifyProductPriceRequest request) {
        Product existedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("가격 수정할 상품이 존재하지 않습니다."));
        existedProduct.modifyPrice(request.price());
        productRepository.save(existedProduct);
    }

    public void modifyStockQuantity(ModifyQuantityRequest request) {
        if (campaignService.isStarted(request.campaignId())) {
            throw new IllegalArgumentException("캠페인이 시작되어 상품을 수정할 수 없습니다.");
        }
        Product existedProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품 조회에 실패했습니다."));
        Stock existedStock = existedProduct.getStock();
        existedStock.modifyTotalQuantity(request.modifyQuantity());
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 상품이 존재하지 않습니다."));
        productRepository.deleteById(id);
    }
}
