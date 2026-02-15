package ee.lio.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String tokenHash;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    private Instant expiredAt;

    private boolean used = false;

    public PasswordResetToken() {
    }

    public PasswordResetToken(Integer id,
                              String tokenHash,
                              User user,
                              Instant expiredAt,
                              boolean used) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiredAt = expiredAt;
        this.used = used;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
