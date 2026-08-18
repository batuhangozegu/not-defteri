package com.notdefteri.dto;

import java.util.UUID;

/**
 * pgvector benzerlik aramasından dönen tek bir sonuç.
 */
public record SimilarChunkDto(UUID pageId, String pageTitle, String chunkText, double distance) {
}
