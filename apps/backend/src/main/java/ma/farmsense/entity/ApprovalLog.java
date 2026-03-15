package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "approval_logs")
@Access(AccessType.FIELD)
public class ApprovalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ApprovalLog() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Transaction getTransaction() { return this.transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public ApprovalAction getAction() { return this.action; }
    public void setAction(ApprovalAction action) { this.action = action; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public String getComment() { return this.comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
