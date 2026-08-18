package com.notdefteri.service;

import com.notdefteri.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Ollama'nın yerel /api/embeddings uç noktasıyla konuşur (varsayılan model: nomic-embed-text).
 * İleride Raspberry Pi üzerinde çalışacak; base URL application.yml'de OLLAMA_BASE_URL ile ayarlanır.
 */
@Service
@Slf4j
public class OllamaEmbeddingClient {

    private final WebClient ollamaWebClient;
    private final AppProperties props;

    public OllamaEmbeddingClient(WebClient ollamaWebClient, AppProperties props) {
        this.ollamaWebClient = ollamaWebClient;
        this.props = props;
    }

    @SuppressWarnings("unchecked")
    public float[] embed(String text) {
        Map<String, Object> body = Map.of(
                "model", props.ollama().embeddingModel(),
                "prompt", text
        );
        Map<String, Object> response = ollamaWebClient.post()
                .uri("/api/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !(response.get("embedding") instanceof List<?> raw)) {
            throw new IllegalStateException("Ollama embedding yanıtı beklenen formatta değil");
        }
        float[] embedding = new float[raw.size()];
        for (int i = 0; i < raw.size(); i++) {
            embedding[i] = ((Number) raw.get(i)).floatValue();
        }
        return embedding;
    }
}
