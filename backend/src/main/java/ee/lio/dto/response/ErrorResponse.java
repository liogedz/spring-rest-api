package ee.lio.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(String message, int status,
                            LocalDateTime timestamp) {
}