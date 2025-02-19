package cholog.wiseshop.api.campaign.dto.response;

import cholog.wiseshop.db.product.Product;

public record MemberProductResponse(
    Long id,
    String name,
    String description,
    Integer price,
    Integer totalQuantity
) {

    public static MemberProductResponse from(Product product) {
        return new MemberProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock().getTotalQuantity()
        );
    }
}
