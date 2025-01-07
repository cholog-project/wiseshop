package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductPriceRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Long createProduct(CreateProductRequest request) {
        Product createdProduct = productRepository.save(request.from());
        return createdProduct.getId();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return new ProductResponse(productRepository.findById(id).orElseThrow());
    }

    public void modifyProduct(ModifyProductRequest request) {
        Product existedProduct= productRepository.findById(request.productId()).orElseThrow();
        existedProduct.modifyProduct(request.name(), request.description());
        productRepository.save(existedProduct);
    }

    public void modifyProductPrice(ModifyProductPriceRequest request) {
        Product existedProduct = productRepository.findById(request.productId()).orElseThrow();
        existedProduct.modifyPrice(request.price());
        productRepository.save(existedProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
