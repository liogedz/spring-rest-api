package ee.lio.dto.request;

import jakarta.validation.constraints.Size;

public record SavePassword(
        @Size(min = 8, max = 100)
        String password
) {
}
