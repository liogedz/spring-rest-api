package ee.lio.service;

public interface TwoFactorService {
    String generateAndStoreCode(String identifier);

    void validateCode(String identifier,
                      String submittedCode);

    void clearCode(String identifier);
}