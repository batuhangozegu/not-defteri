package com.notdefteri.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * "Authorization: Bearer &lt;jwt&gt;" başlığını okuyup geçerliyse SecurityContext'e
 * kullanıcı id'sini (UUID) principal olarak, rolünü de "ROLE_ADMIN"/"ROLE_USER" authority'si
 * olarak koyar (bkz. SecurityConfig'teki hasRole("ADMIN") kuralı). Token yoksa/geçersizse
 * hiçbir şey yapmadan zincire devam eder; erişim kararını {@link SecurityConfig} verir.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            JwtService.ParsedToken parsed = jwtService.parse(header.substring(7));
            if (parsed != null) {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + parsed.role()));
                var authentication = new UsernamePasswordAuthenticationToken(parsed.userId(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
