package com.notdefteri.exception;

/** Hesap henüz bir yönetici tarafından onaylanmadığında giriş denemesinde fırlatılır. */
public class PendingApprovalException extends RuntimeException {
    public PendingApprovalException() {
        super("Hesabınız henüz onaylanmadı. Bir yöneticinin onayını bekleyin.");
    }
}
