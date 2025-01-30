package cholog.wiseshop.exception;

public enum WiseShopErrorCode {

    ALREADY_EXIST_MEMBER("이미 존재하는 회원입니다."),
    MEMBER_ID_NOT_FOUND("올바르지 않은 아이디 입니다."),
    PRODUCT_NOT_FOUND("상품이 존재하지 않습니다."),
    ORDER_LIMIT_EXCEED("주문 가능한 수량을 초과하였습니다. 주문 가능한 수량 : %d개"),
    CAMPAIGN_NOT_IN_PROGRESS("현재 캠페인이 진행 중이지 않습니다."),
    ORDER_NOT_FOUND("주문 정보가 존재하지 않습니다.")
    ;

    private String message;

    WiseShopErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
