package fr.miniastra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration CORS de l'application.
 *
 * <p>Autorise par défaut le frontend de développement Vite ({@code http://localhost:5173}).
 * Des origines supplémentaires peuvent être ajoutées via la propriété
 * {@code app.cors.allowed-origins} (valeurs séparées par des virgules).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String DEV_ORIGIN = "http://localhost:5173";

    /**
     * Origines supplémentaires issues de la configuration applicative.
     * Vide par défaut — surcharger dans application-prod.yml si nécessaire.
     */
    @Value("${app.cors.allowed-origins:}")
    private String additionalAllowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(resolveAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept")
                // false : on n'émet pas de cookies cross-origin — pas de session côté client.
                // Si on passait à true, allowedOrigins ne peut plus contenir "*".
                .allowCredentials(false);
    }

    private String[] resolveAllowedOrigins() {
        List<String> origins = new ArrayList<>();
        origins.add(DEV_ORIGIN);

        if (StringUtils.hasText(additionalAllowedOrigins)) {
            for (String origin : additionalAllowedOrigins.split(",")) {
                String trimmed = origin.trim();
                if (!trimmed.isEmpty()) {
                    origins.add(trimmed);
                }
            }
        }

        return origins.toArray(new String[0]);
    }
}
