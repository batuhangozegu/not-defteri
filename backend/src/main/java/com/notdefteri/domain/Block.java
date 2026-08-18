package com.notdefteri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Bir sayfaya ait tek bir içerik bloğu (paragraf, başlık, todo, madde vb.).
 */
@Entity
@Table(name = "block")
@Getter
@Setter
@NoArgsConstructor
public class Block {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BlockType type;

    @Lob
    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    /** Sadece {@link BlockType#TODO} için anlamlıdır. */
    private Boolean checked;
}
