package com.notdefteri.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Sidebar'daki hiyerarşik sayfa ağacı için iç içe DTO. */
public record PageTreeDto(
        UUID id,
        String title,
        String icon,
        Instant updatedAt,
        List<PageTreeDto> children
) {
}
