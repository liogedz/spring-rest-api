package ee.lio.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_name_unique", columnNames = "name"),
                @UniqueConstraint(name = "user_email_unique", columnNames = "email")
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private boolean enabled;

    private boolean confirmed;

    public User() {
    }

    public User(Integer id,
                String name,
                String email,
                String password,
                Role role,
                String providerId,
                AuthProvider provider,
                boolean enabled,
                boolean confirmed) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.providerId = providerId;
        this.provider = provider;
        this.enabled = enabled;
        this.confirmed = confirmed;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && confirmed == user.confirmed && Objects.equals(id,
                user.id) && Objects.equals(name,
                user.name) && Objects.equals(email,
                user.email) && Objects.equals(password,
                user.password) && role == user.role && Objects.equals(providerId,
                user.providerId) && provider == user.provider;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                name,
                email,
                password,
                role,
                providerId,
                provider,
                enabled,
                confirmed);
    }
}
