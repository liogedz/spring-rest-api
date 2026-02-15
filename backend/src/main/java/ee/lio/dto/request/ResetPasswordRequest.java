package ee.lio.dto.request;

public record ResetPasswordRequest(
        String token,
        String password
) {
}
