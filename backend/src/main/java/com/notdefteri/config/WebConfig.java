package com.notdefteri.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * İzin verilen origin'ler ${CORS_ALLOWED_ORIGINS} ile ayarlanır (virgülle ayrılmış liste,
 * örn. "http://localhost:5173,http://192.168.1.50:5173"). Varsayılan "*": bu API sadece
 * Authorization header'ı ile (cookie/credential yok) doğrulama yaptığı için wildcard origin
 * güvenlik açısından sorun oluşturmaz, ve kişisel/LAN kullanımda (örn. Raspberry Pi) hangi
 * adresten erişileceği önceden bilinmeyebilir.
 *
 * allowPrivateNetwork(true): Chrome'un Private Network Access (RFC1918) politikası,
 * HTTPS olmayan bir sayfadan (örn. Pi'nin http://xxx.local:5173 frontend'i) "daha özel"
 * bir adres alanındaki (örn. aynı Pi'nin 8080 portu) bir kaynağa istek atarken, hedefin
 * preflight yanıtında bunu açıkça izin vermesini şart koşuyor — aksi halde
 * "not a secure context and the resource is in more-private address space" hatasıyla
 * engelleniyor. Bu olmadan LAN/Pi üzerinden .local adresiyle erişim çalışmaz.
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties props;

    public WebConfig(AppProperties props) {
        this.props = props;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowed = props.cors() != null && props.cors().allowedOrigins() != null
                ? props.cors().allowedOrigins()
                : "*";
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowed.split("\\s*,\\s*"))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowPrivateNetwork(true);
    }
}
