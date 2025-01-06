package cholog.wiseshop.api.product.service;

import cholog.wiseshop.api.product.dto.request.CreateProductRequest;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Long createProduct(CreateProductRequest request) {
        Product createdProduct = productRepository.save(request.from());
        return createdProduct.getId();
    }
}
