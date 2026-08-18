package com.notdefteri.controller;

import com.notdefteri.domain.User;
import com.notdefteri.dto.AdminUserDto;
import com.notdefteri.exception.ForbiddenException;
import com.notdefteri.exception.NotFoundException;
import com.notdefteri.repository.UserRepository;
import com.notdefteri.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Kullanıcı onayı için basit yönetim uç noktaları — her metod önce {@link #requireAdmin()} çağırır. */
@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public AdminController(UserRepository userRepository, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<AdminUserDto> list() {
        requireAdmin();
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::isApproved).thenComparing(User::getCreatedAt))
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/{id}/approve")
    public AdminUserDto approve(@PathVariable UUID id) {
        requireAdmin();
        User user = findOrThrow(id);
        user.setApproved(true);
        return toDto(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable UUID id) {
        requireAdmin();
        if (id.equals(currentUser.id())) {
            throw new IllegalArgumentException("Kendi hesabınızı buradan silemezsiniz");
        }
        User user = findOrThrow(id);
        userRepository.delete(user);
    }

    private void requireAdmin() {
        if (!currentUser.isAdmin()) {
            throw new ForbiddenException("Bu işlem için yönetici yetkisi gerekiyor");
        }
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + id));
    }

    private AdminUserDto toDto(User user) {
        return new AdminUserDto(user.getId(), user.getEmail(), user.getDisplayName(),
                user.getRole().name(), user.isApproved(), user.getCreatedAt());
    }
}
