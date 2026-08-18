package com.notdefteri.dto;

import jakarta.validation.constraints.NotBlank;

/** Seçili metin üzerinde AI aksiyonu (özetle/genişlet/düzelt) için istek gövdesi. */
public record AiTextRequest(@NotBlank String text) {
}
