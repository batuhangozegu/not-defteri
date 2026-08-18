package com.notdefteri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Bir sayfa metin parçasının (chunk) embedding vektörünü tutar.
 *
 * {@code embedding} kolonu pgvector "vector(768)" tipindedir (nomic-embed-text boyutu).
 * Bu alan Hibernate tarafından yazılmaz (insertable/updatable=false): yazma ve benzerlik
 * sorguları {@link com.notdefteri.repository.PageEmbeddingRepository} içinde JDBC ile,
 * pgvector'un beklediği metinsel vektör formatı üzerinden yapılır. Tablo ve "vector"
 * eklentisi schema.sql ile oluşturulur.
 */
@Entity
@Table(name = "page_embedding")
@Getter
@Setter
@NoArgsConstructor
public class PageEmbedding {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Lob
    @Column(name = "chunk_text", columnDefinition = "text")
    private String chunkText;

    @Column(columnDefinition = "vector(768)", insertable = false, updatable = false)
    private String embedding;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
