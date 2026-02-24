package ee.lio.security;

import ee.lio.config.AppConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Component
public class CorsHandler implements CorsConfigurationSource {
    private final AppConfig appConfig;

    public CorsHandler(AppConfig appConfig) {
        this.appConfig = appConfig;
    }


    @Override
    public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
        List<String> origins = appConfig.getAllowedOrigins();
        CorsConfiguration returnValue = new CorsConfiguration();
        returnValue.setAllowedOriginPatterns(origins);
        returnValue.setAllowCredentials(true);
        returnValue.setAllowedHeaders(Arrays.asList(
                "Access-Control-Allow-Headers",
                "Access-Control-Allow-Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Origin",
                "Cache-Control",
                "Content-Type",
                "Authorization"
        ));
        returnValue.setAllowedMethods(Arrays.asList(
                "DELETE",
                "GET",
                "POST",
                "PATCH",
                "PUT"
        ));
        return returnValue;
    }


}
