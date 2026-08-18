package com.notdefteri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Hiyerarşik bir sayfa (kendi kendine referans veren parent-child yapısı).
 */
@Entity
@Table(name = "page")
@Getter
@Setter
@NoArgsConstructor
public class Page {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String title;

    /** Sayfa ikonu olarak kullanılan emoji, opsiyonel. */
    private String icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Page parent;

    /** Sayfanın sahibi; her kullanıcı sadece kendi sayfalarını görür/arar/AI ile sorgular. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
