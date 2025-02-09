package cholog.wiseshop.exception;

public enum WiseShopErrorCode {

    ALREADY_EXIST_MEMBER("이미 존재하는 회원입니다."),
    MEMBER_ID_NOT_FOUND("올바르지 않은 아이디 입니다."),
    MEMBER_SESSION_NOT_EXIST("올바르지 않은 접근입니다."),
    PRODUCT_NOT_FOUND("상품이 존재하지 않습니다."),
    MODIFY_NAME_DESCRIPTION_PRODUCT_NOT_FOUND("이름 및 설명글 수정할 상품이 존재하지 않습니다."),
    MODIFY_PRICE_PRODUCT_NOT_FOUND("가격 수정할 상품이 존재하지 않습니다."),
    ORDER_LIMIT_EXCEED("주문 가능한 수량을 초과하였습니다. 주문 가능한 수량 : %d개"),
    CAMPAIGN_NOT_FOUND("존재하지 않는 캠페인 입니다."),
    CAMPAIGN_NOT_IN_PROGRESS("현재 캠페인이 진행 중이지 않습니다."),
    CAMPAIGN_ALREADY_IN_PROGRESS("캠페인이 이미 진행 중 입니다."),
    CAMPAIGN_INVALID_START_DATE("캠페인의 시작 날짜는 현재로부터 24시간을 넘을 수 없습니다."),
    STOCK_NOT_AVAILABLE("재고 수량은 최소 1개 이상이어야 합니다."),
    ORDER_NOT_FOUND("주문 정보가 존재하지 않습니다."),
    ORDER_NOT_AVAILABLE("자신이 만든 캠페인은 주문이 불가능합니다.")
    ;

    private String message;

    WiseShopErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
