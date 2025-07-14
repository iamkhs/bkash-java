package me.iamkhs.bkashjava.service;

import me.iamkhs.bkashjava.config.BkashConfig;
import me.iamkhs.bkashjava.dto.BkashTokenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BkashService {

    private final RestClient restClient = RestClient.create();
    private final BkashConfig config;

    public BkashService(BkashConfig config) {
        this.config = config;
    }

    public String getToken() {
        String url = config.getBaseUrl() + "/tokenized/checkout/token/grant";

        Map<String, String> body = Map.of(
                "app_key", config.getAppKey(),
                "app_secret", config.getAppSecret()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("username", config.getUsername());
        headers.set("password", config.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        BkashTokenResponse response = restClient.post()
                .uri(url)
                .headers(http -> http.addAll(headers))
                .body(body)
                .retrieve()
                .body(BkashTokenResponse.class);

        return response != null ? response.id_token() : null;
    }

    public String createPayment(String token) {
        String url = config.getBaseUrl() + "/tokenized/checkout/create";
        Map<String, Object> payload = Map.of(
                "mode", "0011",
                "payerReference", "017XXXXXXXX",
                "callbackURL", "http://localhost:8080/bkash/callback",
                "amount", "100",
                "currency", "BDT",
                "intent", "sale",
                "merchantInvoiceNumber", "Inv123456"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("X-APP-Key", config.getAppKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> response = restClient.post()
                .uri(url)
                .headers(http -> http.addAll(headers))
                .body(payload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return Objects.requireNonNull(response).get("bkashURL").toString();
    }


    public String executePayment(String paymentId, String token) {
        String url = config.getBaseUrl() + "/tokenized/checkout/execute";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.set("X-APP-Key", config.getAppKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> body = Map.of("paymentID", paymentId);

        return restClient.post()
                .uri(url)
                .headers(http -> http.addAll(headers))
                .body(body)
                .retrieve()
                .body(String.class);
    }
}