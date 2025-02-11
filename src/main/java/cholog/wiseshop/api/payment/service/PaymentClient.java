package cholog.wiseshop.api.payment.service;

import cholog.wiseshop.api.payment.dto.PaymentRequest;
import cholog.wiseshop.db.payment.Payment;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private static final String TOSS_PAYMENTS_API_URL = "https://api.tosspayments.com/v1/payments";

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    private final RestClient restClient;

    public PaymentClient() {
        this.restClient = RestClient.builder()
            .baseUrl(TOSS_PAYMENTS_API_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public Payment confirm(@RequestBody PaymentRequest request) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":")
            .getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        ResponseEntity<Payment> response = restClient.post()
            .uri(TOSS_PAYMENTS_API_URL + "/confirm")
            .header(HttpHeaders.AUTHORIZATION, authorizations)
            .body(request)
            .retrieve()
            .toEntity(Payment.class);

        return response.getBody();
    }
}
