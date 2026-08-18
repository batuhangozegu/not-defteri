package com.notdefteri.service;

import java.util.UUID;

/**
 * Bir sayfanın içeriği (başlık veya blokları) değiştiğinde yayınlanır. Embedding yeniden
 * üretimi bu event'i transaction commit olduktan SONRA dinler (bkz. {@link EmbeddingService}),
 * aksi halde arka plan iş parçacığı henüz commit edilmemiş veriyi göremeyebilir.
 */
public record PageChangedEvent(UUID pageId) {
}
