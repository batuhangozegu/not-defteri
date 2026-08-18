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
 * Uygulama kullanıcısı. Her kullanıcı sadece kendi oluşturduğu sayfaları görür
 * (bkz. {@link Page#getOwner()}). Tablo adı "user" yerine "app_user": bazı
 * veritabanlarında (PostgreSQL dahil) "user" ayrılmış/özel anlamlı bir isimdir.
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String displayName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
