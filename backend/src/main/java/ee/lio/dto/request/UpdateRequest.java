package ee.lio.dto.request;

import ee.lio.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRequest(
        @NotNull @Size(min = 2, max = 20)
        String name,
        @NotNull @Email
        String email,
        @NotNull @Size(min = 8)
        String password,
        @NotNull
        Role role
) {
}
