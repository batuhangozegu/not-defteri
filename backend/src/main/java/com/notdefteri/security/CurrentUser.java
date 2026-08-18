package com.notdefteri.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** İstek üzerindeki giriş yapmış kullanıcının id'sine erişim için yardımcı. */
@Component
public class CurrentUser {

    public UUID id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UUID id)) {
            throw new IllegalStateException("Kimliği doğrulanmış kullanıcı bulunamadı");
        }
        return id;
    }
}
