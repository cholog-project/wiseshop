package cholog.wiseshop.api.product.dto.request;

import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.stock.Stock;

public record CreateProductRequest(
    String name,
    String description,
    int price,
    int totalQuantity
) {
    public Product from() {
        return Product.builder()
            .name(name)
            .description(description)
            .price(price)
            .stock(new Stock(totalQuantity))
            .build();
    }
}
