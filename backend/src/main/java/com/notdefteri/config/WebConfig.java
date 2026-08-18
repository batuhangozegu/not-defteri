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
                .allowedHeaders("*");
    }
}
