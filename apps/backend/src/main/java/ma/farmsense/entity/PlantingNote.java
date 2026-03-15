package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "planting_notes")
@Access(AccessType.FIELD)
public class PlantingNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planting_id", nullable = false)
    private Planting planting;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public PlantingNote() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Planting getPlanting() { return this.planting; }
    public void setPlanting(Planting planting) { this.planting = planting; }

    public String getContent() { return this.content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
