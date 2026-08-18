package com.notdefteri.service;

import com.notdefteri.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini generateContent uç noktasına istek atar. 429 (kota) veya 503 gibi geçici
 * hatalarda üstel geri çekilmeli (exponential backoff) yeniden dener.
 */
@Service
@Slf4j
public class GeminiClient {

    private final WebClient geminiWebClient;
    private final AppProperties props;

    public GeminiClient(WebClient geminiWebClient, AppProperties props) {
        this.geminiWebClient = geminiWebClient;
        this.props = props;
    }

    public String generateContent(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        Map<String, Object> response = geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", props.gemini().apiKey())
                        .build(props.gemini().model()))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(Retry.backoff(4, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(30))
                        .filter(this::isRetryable)
                        .doBeforeRetry(signal -> log.warn(
                                "Gemini isteği yeniden deneniyor (deneme {}): {}",
                                signal.totalRetries() + 1, signal.failure().getMessage())))
                .block();

        return extractText(response);
    }

    private boolean isRetryable(Throwable throwable) {
        if (throwable instanceof WebClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            return status.value() == 429 || status.is5xxServerError();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) return "";
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Gemini yanıtı ayrıştırılamadı: {}", response, e);
            throw new IllegalStateException("Gemini yanıtı ayrıştırılamadı", e);
        }
    }
}
