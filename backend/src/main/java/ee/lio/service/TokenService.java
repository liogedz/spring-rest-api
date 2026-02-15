package ee.lio.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    void handleForgotPassword(String email);

    void validateResetToken(String rawToken);

    void resetPassword(String rawToken,
                       String password);
}
