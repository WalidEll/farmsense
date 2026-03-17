package ma.farmsense.entity;

public enum FlockStatus {
    ACTIVE, SOLD, PHASED_OUT,
    /** Legacy value kept for backward compatibility with existing records. */
    FINISHED
}
