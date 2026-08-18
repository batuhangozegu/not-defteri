package com.notdefteri.service;

import com.notdefteri.domain.Block;
import com.notdefteri.domain.Page;
import com.notdefteri.repository.BlockRepository;
import com.notdefteri.repository.PageEmbeddingRepository;
import com.notdefteri.repository.PageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bir sayfa kaydedildiğinde/güncellendiğinde blokların metnini parçalara (chunk) ayırıp
 * Ollama ile embed eder ve pgvector tablosuna yazar. Bu iş sayfa kaydını yavaşlatmamak için
 * arka planda (async) çalışır; embedding üretimi başarısız olsa da sayfa/blok kaydı etkilenmez.
 */
@Service
@Slf4j
public class EmbeddingService {

    private static final int CHUNK_SIZE = 1000;

    private final PageRepository pageRepository;
    private final BlockRepository blockRepository;
    private final PageEmbeddingRepository pageEmbeddingRepository;
    private final OllamaEmbeddingClient ollamaEmbeddingClient;

    public EmbeddingService(PageRepository pageRepository,
                             BlockRepository blockRepository,
                             PageEmbeddingRepository pageEmbeddingRepository,
                             OllamaEmbeddingClient ollamaEmbeddingClient) {
        this.pageRepository = pageRepository;
        this.blockRepository = blockRepository;
        this.pageEmbeddingRepository = pageEmbeddingRepository;
        this.ollamaEmbeddingClient = ollamaEmbeddingClient;
    }

    @Async("embeddingTaskExecutor")
    public void reindexPage(UUID pageId) {
        try {
            Page page = pageRepository.findById(pageId).orElse(null);
            if (page == null) return;

            List<Block> blocks = blockRepository.findByPageIdOrderByOrderIndexAsc(pageId);
            String fullText = (page.getTitle() + "\n" + joinBlockText(blocks)).trim();

            pageEmbeddingRepository.deleteByPageId(pageId);
            if (fullText.isBlank()) return;

            for (String chunk : chunk(fullText, CHUNK_SIZE)) {
                float[] embedding = ollamaEmbeddingClient.embed(chunk);
                pageEmbeddingRepository.insert(pageId, chunk, embedding);
            }
            log.info("Sayfa {} için embedding üretimi tamamlandı", pageId);
        } catch (Exception e) {
            // Ollama erişilemez olabilir (örn. Pi henüz kurulmadıysa); sayfa kaydı buna
            // rağmen başarılı sayılmalı, sadece loglanır.
            log.warn("Sayfa {} için embedding üretimi başarısız oldu: {}", pageId, e.getMessage());
        }
    }

    private String joinBlockText(List<Block> blocks) {
        StringBuilder sb = new StringBuilder();
        for (Block block : blocks) {
            if (block.getContent() != null && !block.getContent().isBlank()) {
                sb.append(block.getContent()).append("\n");
            }
        }
        return sb.toString();
    }

    private List<String> chunk(String text, int size) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += size) {
            chunks.add(text.substring(i, Math.min(text.length(), i + size)));
        }
        return chunks;
    }
}
