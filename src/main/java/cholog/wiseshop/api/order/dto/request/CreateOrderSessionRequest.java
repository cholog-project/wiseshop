package cholog.wiseshop.api.order.dto.request;

public record CreateOrderSessionRequest(String paymentOrderId,
                                        Long amount) {

}
