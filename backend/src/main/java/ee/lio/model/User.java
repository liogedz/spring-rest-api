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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    public User(Integer id,
                String name,
                String email,
                String password,
                Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User engineer = (User) o;
        return Objects.equals(id,
                engineer.id) && Objects.equals(name,
                engineer.name) && Objects.equals(email,
                engineer.email) && Objects.equals(password,
                engineer.password) && role == engineer.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                name,
                email,
                password,
                role);
    }
}
