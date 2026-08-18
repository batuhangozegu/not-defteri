package com.notdefteri.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PageUpsertRequest(
        @NotBlank String title,
        String icon,
        UUID parentId
) {
}
