package com.notdefteri.dto;

import java.util.UUID;

/**
 * {@code token} sadece hesap onaylıysa doludur; onay bekleyen bir kayıt/giriş denemesinde
 * {@code approved=false} ve {@code token=null} döner — istemci bunu "onay bekliyor" ekranı
 * göstermek için kullanır.
 */
public record AuthResponse(String token, UUID id, String email, String displayName, String role, boolean approved) {
}
