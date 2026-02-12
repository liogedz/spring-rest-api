package ee.lio.security;

import ee.lio.model.User;
import ee.lio.service.EmailService;
import ee.lio.service.TwoFactorService;
import ee.lio.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${redirectVerifyUrl}")
    private String redirectUrl;

    @Value("${redirectRegisterUrl}")
    private String registerUrl;

    private final UserService userService;
    private final TwoFactorService twoFactorService;
    private final EmailService emailService;

    public OAuth2SuccessHandler(UserService userService,
                                TwoFactorService twoFactorService,
                                EmailService emailService) {
        this.userService = userService;
        this.twoFactorService = twoFactorService;
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String registrationId =
                ((OAuth2AuthenticationToken) authentication)
                        .getAuthorizedClientRegistrationId();

        User user = userService.findOrCreateOAuthUser(
                oauthUser,
                registrationId
        );
        String userEmail = user.getEmail();
        String code = twoFactorService.generateAndStoreCode(userEmail);
        emailService.send2FACode(userEmail,
                code);

        response.sendRedirect(redirectUrl + userEmail);
    }
}
