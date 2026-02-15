package ee.lio.service;

public interface EmailService {
    void send2FACode(String to,
                     String code);

    void sendPasswordReset(String email,
                           String address);
}
