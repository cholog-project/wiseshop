package cholog.wiseshop.api.product.dto.request;

public record ModifyProductRequest(Long productId,
                                   String name,
                                   String description) {
}
