package com.notdefteri.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("Bu e-posta zaten kayıtlı: " + email);
    }
}
