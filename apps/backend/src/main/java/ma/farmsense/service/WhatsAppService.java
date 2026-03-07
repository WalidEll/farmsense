package ma.farmsense.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class WhatsAppService {

    private final WebClient webClient;
    private final String phoneNumberId;
    private final String token;

    public WhatsAppService(
            WebClient.Builder webClientBuilder,
            @Value("${farmsense.whatsapp.api-url}") String apiUrl,
            @Value("${farmsense.whatsapp.phone-number-id}") String phoneNumberId,
            @Value("${farmsense.whatsapp.token}") String token) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.phoneNumberId = phoneNumberId;
        this.token = token;
    }

    @Async
    public void send(String toPhone, String message) {
        if (token == null || token.isBlank()) {
            log.warn("WhatsApp token not configured — skipping message to {}", toPhone);
            return;
        }

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", toPhone,
                "type", "text",
                "text", Map.of("body", message)
        );

        webClient.post()
                .uri("/{phoneNumberId}/messages", phoneNumberId)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("WhatsApp send failed to {}: {}", toPhone, e.getMessage()))
                .subscribe(resp -> log.debug("WhatsApp sent to {}", toPhone));
    }
}
