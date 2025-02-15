package cholog.wiseshop.api.product.dto.request;

public record ModifyProductPriceAntStockRequest(
    int price,
    int totalQuantity
) {

}
