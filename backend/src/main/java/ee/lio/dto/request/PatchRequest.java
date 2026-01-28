package ee.lio.dto.request;

import ee.lio.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record PatchRequest(
        @Size(min = 3, max = 20)
        String name,
        @Email
        String email,
        @Size(min = 8)
        String password,
        Role role
) {
}
