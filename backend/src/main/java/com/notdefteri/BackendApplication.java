package com.notdefteri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

// UserDetailsServiceAutoConfiguration hariç tutuldu: kimlik doğrulama tamamen JWT ile
// (bkz. security paketi) yapılıyor, Spring Security'nin varsayılan in-memory kullanıcısına
// (rastgele üretilip loglanan şifreye) hiç ihtiyaç yok.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
