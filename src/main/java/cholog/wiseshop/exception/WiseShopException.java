package cholog.wiseshop.exception;

public class WiseShopException extends RuntimeException {

    private final String errorCode;

    public WiseShopException(WiseShopErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.name();
    }

    public WiseShopException(String message,
                             Throwable cause,
                             WiseShopErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode.name();
    }

    public WiseShopException(WiseShopErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode.name();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
