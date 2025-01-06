package cholog.wiseshop.api.product.dto.response;

import cholog.wiseshop.db.product.Product;

public record ProductResponse(String name,
                              String description,
                              int price) {

    public ProductResponse(Product product) {
        this(
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
