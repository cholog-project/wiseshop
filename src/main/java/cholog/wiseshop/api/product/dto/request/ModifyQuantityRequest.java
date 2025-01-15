package cholog.wiseshop.api.product.dto.request;

public record ModifyQuantityRequest(Long productId,
                                    Integer modifyQuantity) {
}
