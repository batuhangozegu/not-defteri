package com.notdefteri.service;

import com.notdefteri.dto.AskResponse;
import com.notdefteri.dto.SimilarChunkDto;
import com.notdefteri.repository.PageEmbeddingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Basit bir RAG akışı: soruyu embed et, pgvector ile en alakalı parçaları bul,
 * bulunan bağlam + soruyu Gemini'ye gönder.
 */
@Service
public class RagService {

    private static final int TOP_K = 5;

    private final OllamaEmbeddingClient ollamaEmbeddingClient;
    private final PageEmbeddingRepository pageEmbeddingRepository;
    private final GeminiClient geminiClient;

    public RagService(OllamaEmbeddingClient ollamaEmbeddingClient,
                       PageEmbeddingRepository pageEmbeddingRepository,
                       GeminiClient geminiClient) {
        this.ollamaEmbeddingClient = ollamaEmbeddingClient;
        this.pageEmbeddingRepository = pageEmbeddingRepository;
        this.geminiClient = geminiClient;
    }

    public AskResponse ask(UUID ownerId, String question) {
        float[] questionEmbedding = ollamaEmbeddingClient.embed(question);
        List<SimilarChunkDto> matches = pageEmbeddingRepository.findMostSimilar(ownerId, questionEmbedding, TOP_K);

        if (matches.isEmpty()) {
            return new AskResponse(
                    "Sayfalarında bu soruyla ilgili bir içerik bulamadım. Önce ilgili sayfaya biraz " +
                            "içerik ekleyip tekrar sorabilirsin.",
                    List.of());
        }

        String context = buildContext(matches);
        String prompt = """
                Sen kullanıcının kişisel not uygulamasındaki sayfalar üzerinde çalışan bir asistansın.
                Aşağıda kullanıcının notlarından alınmış parçalar var. Sadece bu parçalardaki bilgiye
                dayanarak Türkçe ve kısa şekilde cevap ver; bağlamda olmayan bir şeyi uydurma, emin
                değilsen belirt.

                Bağlam:
                %s

                Soru: %s
                """.formatted(context, question);

        String answer = geminiClient.generateContent(prompt);

        List<AskResponse.SourceDto> sources = matches.stream()
                .map(m -> new AskResponse.SourceDto(
                        m.pageId().toString(),
                        m.pageTitle(),
                        snippet(m.chunkText())))
                .toList();

        return new AskResponse(answer, sources);
    }

    private String buildContext(List<SimilarChunkDto> matches) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (SimilarChunkDto m : matches) {
            sb.append("[").append(i++).append("] (").append(m.pageTitle()).append(")\n")
                    .append(m.chunkText()).append("\n\n");
        }
        return sb.toString();
    }

    private String snippet(String text) {
        return text.length() > 160 ? text.substring(0, 160) + "…" : text;
    }
}
