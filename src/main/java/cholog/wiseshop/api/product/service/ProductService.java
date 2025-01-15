package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyQuantityRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
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
        Product product = new Product(request.name(), request.description(), request.price(), stock);
        Product createdProduct = productRepository.save(product);
        return createdProduct.getId();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return new ProductResponse(productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 조회에 실패했습니다.")));
    }

    public void modifyProduct(ModifyProductRequest request) {
        Product existedProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("이름 및 설명글 수정할 상품이 존재하지 않습니다."));
        existedProduct.modifyProduct(request.name(), request.description());
        productRepository.save(existedProduct);
    }

    public void modifyProductPrice(ModifyProductPriceRequest request) {
        Product existedProduct = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("가격 수정할 상품이 존재하지 않습니다."));
        existedProduct.modifyPrice(request.price());
        productRepository.save(existedProduct);
    }

    public void modifyStockQuantity(ModifyQuantityRequest request) {
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
