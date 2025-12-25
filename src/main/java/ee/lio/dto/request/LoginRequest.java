package ee.lio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull
        String identifier,
        @NotNull @Size(min = 8)
        String password
) {
}
