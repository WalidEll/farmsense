package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    @Builder.Default
    private Language lang = Language.FR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Tier tier = Tier.FREE;

    private String claimToken;
    private Instant claimTokenExpiresAt;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

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
