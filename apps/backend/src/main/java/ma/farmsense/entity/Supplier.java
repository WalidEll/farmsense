package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Access(AccessType.FIELD)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "products_supplied", columnDefinition = "TEXT")
    private String productsSupplied;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Supplier() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return this.phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return this.address; }
    public void setAddress(String address) { this.address = address; }

    public String getProductsSupplied() { return this.productsSupplied; }
    public void setProductsSupplied(String productsSupplied) { this.productsSupplied = productsSupplied; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
