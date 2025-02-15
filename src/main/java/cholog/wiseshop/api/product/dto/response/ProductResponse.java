package cholog.wiseshop.api.product.dto.response;

import cholog.wiseshop.db.product.Product;

public record ProductResponse(
    Long id,
    String name,
    String description,
    Integer price,
    Integer totalQuantity,
    Long ownerId
) {

    public ProductResponse(Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock().getTotalQuantity(),
            product.getOwner().getId()
        );
    }
}
