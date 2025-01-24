package cholog.wiseshop.api.product.dto.request;

public record ModifyQuantityRequest(Long campaignId,
                                    Long productId,
                                    Integer modifyQuantity) {

}
