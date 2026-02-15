package ee.lio.service.impl;

import ee.lio.exceptions.ForbiddenException;
import ee.lio.exceptions.ResourceNotFoundException;
import ee.lio.model.AuthProvider;
import ee.lio.model.PasswordResetToken;
import ee.lio.model.User;
import ee.lio.repository.TokenRepository;
import ee.lio.repository.UserRepository;
import ee.lio.service.EmailService;
import ee.lio.service.TokenService;
import ee.lio.utils.TokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TokenServiceImpl implements TokenService {

    @Value("${resetPassword}")
    private String resetPassword;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenServiceImpl(UserRepository userRepository,
                            TokenRepository tokenRepository,
                            EmailService emailService,
                            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void handleForgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.getProvider() != AuthProvider.LOCAL) {
                return;
            }
            String rawToken = TokenUtil.generateToken();
            String hashedToken = TokenUtil.hashToken(rawToken);

            PasswordResetToken token = new PasswordResetToken();
            token.setUser(user);
            token.setTokenHash(hashedToken);
            token.setExpiredAt(Instant.now().plus(15,
                    ChronoUnit.MINUTES));
            tokenRepository.save(token);
            emailService.sendPasswordReset(
                    user.getEmail(),
                    resetPassword + rawToken);

        });
    }

    @Override
    public void validateResetToken(String rawToken) {
        String hash = TokenUtil.hashToken(rawToken);
        PasswordResetToken token = tokenRepository
                .findByTokenHash(hash)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid password reset token"));
        if (token.isUsed() || token.getExpiredAt().isBefore(Instant.now())) {
            throw new ForbiddenException("Token has expired");
        }
    }

    @Override
    public void resetPassword(String rawToken,
                              String password) {
        String hash = TokenUtil.hashToken(rawToken);
        PasswordResetToken token = tokenRepository
                .findByTokenHash(hash)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid password reset token"));
        if (token.isUsed() || token.getExpiredAt().isBefore(Instant.now())) {
            throw new ForbiddenException("Token has expired");
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(password));

        token.setUsed(true);
        tokenRepository.save(token);
        userRepository.save(user);
    }
}
