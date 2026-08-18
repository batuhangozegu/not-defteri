package com.notdefteri.dto;

import com.notdefteri.domain.BlockType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * {@code id} null ise yeni blok olarak oluşturulur; doluysa var olan blok güncellenir.
 */
public record BlockDto(
        UUID id,
        @NotNull BlockType type,
        String content,
        int orderIndex,
        Boolean checked
) {
}
