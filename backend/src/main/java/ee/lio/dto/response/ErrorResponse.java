package ee.lio.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(String error, int status,
                            LocalDateTime timestamp) {
}