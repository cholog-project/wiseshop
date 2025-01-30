package cholog.wiseshop.exception;

public enum WiseShopErrorCode {

    ALREADY_EXIST_MEMBER("이미 존재하는 회원입니다."),
    MEMBER_ID_NOT_FOUND("올바르지 않은 아이디 입니다.");

    private String message;

    WiseShopErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
