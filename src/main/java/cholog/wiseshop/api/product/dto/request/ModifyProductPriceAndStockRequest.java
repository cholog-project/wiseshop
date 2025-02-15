package cholog.wiseshop.api.product.dto.request;

public record ModifyProductPriceAndStockRequest(
    int price,
    int totalQuantity
) {

}
