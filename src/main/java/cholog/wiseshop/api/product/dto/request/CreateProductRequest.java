package cholog.wiseshop.api.product.dto.request;

import cholog.wiseshop.db.product.Product;

public record CreateProductRequest(String name,
                                   String description,
                                   Integer price,
                                   Integer totalQuantity) {

    public Product from() {
        return new Product(
                name,
                description,
                price
        );
    }
}
