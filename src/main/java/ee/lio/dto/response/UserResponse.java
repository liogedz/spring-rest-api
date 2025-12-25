package ee.lio.dto.response;

import ee.lio.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserResponse {

    @NotNull
    private Integer id;
    @NotNull
    @Size(min = 3, max = 20)
    private String name;
    @NotNull
    @Email
    private String email;
    @NotNull
    private Role role;
    private String authToken;

    public UserResponse() {
    }

    public UserResponse(Integer id,
                        String name,
                        String email,
                        Role role,
                        String authToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.authToken = authToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
