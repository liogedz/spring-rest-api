package ee.lio.service;

public interface TokenService {
    void handleForgotPassword(String email);

    void validateResetToken(String rawToken);

    void resetPassword(String rawToken,
                       String password);
}
