package com.notdefteri.dto;

import java.time.Instant;
import java.util.UUID;

public record PageResponseDto(
        UUID id,
        String title,
        String icon,
        UUID parentId,
        Instant createdAt,
        Instant updatedAt
) {
}
