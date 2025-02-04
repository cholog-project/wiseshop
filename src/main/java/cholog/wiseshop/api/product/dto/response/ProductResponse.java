package cholog.wiseshop.api.product.dto.response;

import cholog.wiseshop.api.product.domain.ProductModel;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Long price,
        Long totalQuantity
) {

    public ProductResponse(ProductModel product) {
        this(
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.quantity()
        );
    }
}
