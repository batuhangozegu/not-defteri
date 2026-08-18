package com.notdefteri.dto;

import jakarta.validation.Valid;

import java.util.List;

/** Bir sayfanın tüm bloklarını sıralı biçimde değiştirir (upsert + eksik olanları siler). */
public record BlocksReplaceRequest(@Valid List<BlockDto> blocks) {
}
