package com.notdefteri.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml altındaki "app.*" ayarları. Gerçek değerler ortam değişkenlerinden gelir,
 * bu sınıfta veya application.yml içinde hiçbir gerçek anahtar/parola bulunmaz.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Ollama ollama, Gemini gemini, Jwt jwt, Cors cors) {

    public record Ollama(String baseUrl, String embeddingModel) {
    }

    public record Gemini(String apiKey, String model) {
    }

    public record Jwt(String secret, long expirationMinutes) {
    }

    public record Cors(String allowedOrigins) {
    }
}
