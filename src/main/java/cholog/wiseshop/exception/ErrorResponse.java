package cholog.wiseshop.exception;

public record ErrorResponse(
    String code,
    String message,
    String status
) {

}
