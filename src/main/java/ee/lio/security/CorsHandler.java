package ee.lio.security;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Component
public class CorsHandler implements CorsConfigurationSource {

    @Override
    public @Nullable CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
        CorsConfiguration returnValue = new CorsConfiguration();
        returnValue.setAllowedOriginPatterns(List.of("*"));
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
