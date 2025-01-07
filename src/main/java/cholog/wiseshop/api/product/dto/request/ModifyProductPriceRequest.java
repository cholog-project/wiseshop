package cholog.wiseshop.api.product.dto.request;

public record ModifyProductPriceRequest(Long productId,
                                        int price) {
}
