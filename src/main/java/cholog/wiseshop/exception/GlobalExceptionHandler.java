package cholog.wiseshop.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger("GlobalExceptionHandler");

    private static final String LOG_FORMAT = """
        \n\t{
            "RequestURI": "{} {}",
            "ErrorMessage": "{}",
        \t}
        """;

    @ExceptionHandler(WiseShopException.class)
    public ResponseEntity<ErrorResponse> handleWiseshopException(HttpServletRequest request, WiseShopException e) {
        log.warn(LOG_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getErrorCode(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        log.error(LOG_FORMAT, request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse("SERVER_ERROR",
                "서버 에러가 발생하였습니다.",
                HttpStatus.INTERNAL_SERVER_ERROR.toString()));
    }

    private String getRequestBody(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator() + "\t"));
        } catch (IOException e) {
            log.error("Failed to read request body", e);
            return "";
        }
    }
}
