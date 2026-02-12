package ee.lio.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GitHubEmailLoader implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Value("${githubEmailApi}")
    private String githubEmailApi;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final RestClient restClient;

    public GitHubEmailLoader(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        OAuth2User user = delegate.loadUser(userRequest);

        String token = userRequest.getAccessToken().getTokenValue();

        List<Map<String, Object>> emailResponse = restClient.get()
                .uri(githubEmailApi)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        String primaryEmail = null;
        if (emailResponse != null) {
            primaryEmail = emailResponse.stream()
                    .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                    .filter(e -> Boolean.TRUE.equals(e.get("verified")))
                    .map(e -> (String) e.get("email"))
                    .findFirst()
                    .orElse(null);
        }

        Map<String, Object> merged = new HashMap<>(user.getAttributes());
        merged.put("email",
                primaryEmail);

        return new DefaultOAuth2User(user.getAuthorities(),
                merged,
                "login");
    }
}