package cholog.wiseshop.api.product.dto.request;

public record ModifyProductPriceRequest(
    int price,
    int totalQuantity
) {

}
