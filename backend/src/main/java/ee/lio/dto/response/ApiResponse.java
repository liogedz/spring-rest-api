package ee.lio.dto.response;

import jakarta.validation.constraints.NotNull;

public record ApiResponse(
        @NotNull
        String message,
        Object data
) {
}
