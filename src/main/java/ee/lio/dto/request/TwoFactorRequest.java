package ee.lio.dto.request;

public record TwoFactorRequest(String identifier, String code) {
}
