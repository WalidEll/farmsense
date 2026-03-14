package ma.farmsense.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ma.farmsense.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findByUser_id(UUID userId);

    List<StockMovement> findByInventoryItem_id(UUID inventoryItemId);

    List<StockMovement> findByReferenceNumber(String referenceNumber);

    /**
     * Get all movements (in/out) for an inventory item within a date range
     */
    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sm.movementDate DESC, sm.createdAt DESC")
    List<StockMovement> findMovementsByDateRange(
        @Param("inventoryItemId") UUID inventoryItemId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Calculate current stock level for an inventory item
     * (sum of all IN movements - sum of all OUT movements)
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE 0 END), 0) " +
           "- COALESCE(SUM(CASE WHEN sm.type != 'IN' THEN sm.quantity ELSE 0 END), 0) " +
           "FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId")
    BigDecimal calculateCurrentStock(
        @Param("inventoryItemId") UUID inventoryItemId);

    /**
     * Get total movements (in + out) for an item
     */
    @Query("SELECT COALESCE(SUM(sm.quantity), 0) " +
           "FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId")
    BigDecimal calculateTotalMovements(
        @Param("inventoryItemId") UUID inventoryItemId);

    /**
     * Find all IN movements for an inventory item
     */
    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId " +
           "AND sm.type = 'IN'")
    List<StockMovement> findInMovements(
        @Param("inventoryItemId") UUID inventoryItemId);

    /**
     * Find all OUT movements for an inventory item
     */
    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId " +
           "AND sm.type != 'IN'")
    List<StockMovement> findOutMovements(
        @Param("inventoryItemId") UUID inventoryItemId);

    /**
     * Paginated movements for an inventory item
     */
    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId " +
           "ORDER BY sm.movementDate DESC, sm.createdAt DESC")
    Page<StockMovement> findMovementsByInventoryItem(
        @Param("inventoryItemId") UUID inventoryItemId,
        Pageable pageable);

    /**
     * Find movements by supplier
     */
    List<StockMovement> findBySupplier_id(UUID supplierId);

    /**
     * Find movements by customer
     */
    List<StockMovement> findByCustomer_id(UUID customerId);

    /**
     * Find movements by flock
     */
    List<StockMovement> findByFlock_id(UUID flockId);

    /**
     * Get movements with a specific reason
     */
    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE sm.reason = :reason")
    List<StockMovement> findMovementsByReason(
        @Param("reason") String reason);

    /**
     * Search movements by description or notes (partial match)
     */
    @Query("SELECT sm FROM StockMovement sm " +
           "WHERE LOWER(sm.description) LIKE LOWER(:query) " +
           "OR LOWER(sm.notes) LIKE LOWER(:query)")
    List<StockMovement> searchMovements(
        @Param("query") String query);

    /**
     * Count movements by type for a specific inventory item
     */
    @Query("SELECT COUNT(CASE WHEN sm.type = :type THEN 1 END) " +
           "FROM StockMovement sm " +
           "WHERE sm.inventoryItem.id = :inventoryItemId")
    Long countMovementsByType(
        @Param("inventoryItemId") UUID inventoryItemId,
        @Param("type") String type);

}
