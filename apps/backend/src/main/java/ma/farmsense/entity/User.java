package ma.farmsense.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    private String phoneWa;  // WhatsApp number

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language lang = Language.FR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier = Tier.FREE;

    private String claimToken;
    private Instant claimTokenExpiresAt;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public User() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return this.passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneWa() { return this.phoneWa; }
    public void setPhoneWa(String phoneWa) { this.phoneWa = phoneWa; }

    public Language getLang() { return this.lang; }
    public void setLang(Language lang) { this.lang = lang; }

    public Tier getTier() { return this.tier; }
    public void setTier(Tier tier) { this.tier = tier; }

    public String getClaimToken() { return this.claimToken; }
    public void setClaimToken(String claimToken) { this.claimToken = claimToken; }

    public Instant getClaimTokenExpiresAt() { return this.claimTokenExpiresAt; }
    public void setClaimTokenExpiresAt(Instant claimTokenExpiresAt) { this.claimTokenExpiresAt = claimTokenExpiresAt; }

    public Double getLatitude() { return this.latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return this.longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        return name;
    }

    // ── UserDetails ──────────────────────────────────────────
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public enum Language { AR, FR, EN }
    public enum Tier { FREE, HOME_PRO }
}
