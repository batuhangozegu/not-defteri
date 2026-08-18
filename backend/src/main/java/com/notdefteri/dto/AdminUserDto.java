package com.notdefteri.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminUserDto(
        UUID id,
        String email,
        String displayName,
        String role,
        boolean approved,
        Instant createdAt
) {
}
