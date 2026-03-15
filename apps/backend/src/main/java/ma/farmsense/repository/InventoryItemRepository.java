package ma.farmsense.repository;

import ma.farmsense.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    // Find by SKU (exact match)
    Optional<InventoryItem> findBySku(String sku);

    // Find by user
    List<InventoryItem> findByUserId(UUID userId);

    // Check if inventory item exists by SKU and active status
    boolean existsBySkuAndIsActive(String sku, Boolean isActive);

    // Search by category
    List<InventoryItem> findByCategory(String category);

    // Low stock items below reorder level
    @Query("SELECT i FROM InventoryItem i WHERE COALESCE(i.reorderLevel, 0) > 0 AND " +
            "COALESCE(i.currentQuantity, 0) < i.reorderLevel ORDER BY " +
            "((i.reorderLevel - i.currentQuantity) / i.reorderLevel * 100)")
    List<InventoryItem> findLowStockItems();

    // Items expiring soon (within X days)
    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate IS NOT NULL AND " +
            "i.expiryDate <= :expiryDate ORDER BY i.expiryDate")
    List<InventoryItem> findExpiringSoon(@Param("expiryDate") LocalDate expiryDate);

    // Find by location
    List<InventoryItem> findByLocation(String location);

    // Find active items only
    List<InventoryItem> findByIsActiveTrue();

    // Bulk upsert for performance in stock adjustments
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("INSERT INTO inventory_items (id, user_id, team_id, sku, name, name_ar, name_en, " +
            "category, sub_category, description, unit_of_measure, current_quantity, " +
            "min_quantity, max_quantity, reorder_level, unit_cost, purchase_price, sale_price, " +
            "expiry_date, batch_number, location, supplier_id, is_tracked, is_active, notes) " +
            "VALUES (:id, :userId, :teamId, :sku, :name, :nameAr, :nameEn, :category, " +
            ":subCategory, :description, :unitOfMeasure, :currentQuantity, :minQuantity, " +
            ":maxQuantity, :reorderLevel, :unitCost, :purchasePrice, :salePrice, :expiryDate, " +
            ":batchNumber, :location, :supplierId, :isTracked, :isActive, :notes) " +
            "ON CONFLICT (sku) DO UPDATE SET " +
            "name = EXCLUDED.name, name_ar = COALESCE(EXCLUDED.name_ar, inventory_items.name_ar), " +
            "name_en = COALESCE(EXCLUDED.name_en, inventory_items.name_en), " +
            "category = EXCLUDED.category, sub_category = EXCLUDED.sub_category, " +
            "description = COALESCE(EXCLUDED.description, inventory_items.description), " +
            "current_quantity = COALESCE(EXCLUDED.current_quantity, inventory_items.current_quantity), " +
            "min_quantity = COALESCE(EXCLUDED.min_quantity, inventory_items.min_quantity), " +
            "max_quantity = COALESCE(EXCLUDED.max_quantity, inventory_items.max_quantity), " +
            "reorder_level = EXCLUDED.reorder_level, unit_cost = COALESCE(EXCLUDED.unit_cost, inventory_items.unit_cost), " +
            "purchase_price = COALESCE(EXCLUDED.purchase_price, inventory_items.purchase_price), " +
            "sale_price = COALESCE(EXCLUDED.sale_price, inventory_items.sale_price), " +
            "expiry_date = EXCLUDED.expiry_date, batch_number = COALESCE(EXCLUDED.batch_number, inventory_items.batch_number), " +
            "location = COALESCE(EXCLUDED.location, inventory_items.location), " +
            "supplier_id = COALESCE(EXCLUDED.supplier_id, inventory_items.supplier_id), " +
            "notes = COALESCE(EXCLUDED.notes, inventory_items.notes), " +
            "updated_at = CURRENT_TIMESTAMP")
    int upsert(@Param("id") UUID id, @Param("userId") UUID userId, @Param("teamId") UUID teamId,
               @Param("sku") String sku, @Param("name") String name, @Param("nameAr") String nameAr,
               @Param("nameEn") String nameEn, @Param("category") String category,
               @Param("subCategory") String subCategory, @Param("description") String description,
               @Param("unitOfMeasure") String unitOfMeasure, @Param("currentQuantity") BigDecimal currentQuantity,
               @Param("minQuantity") BigDecimal minQuantity, @Param("maxQuantity") BigDecimal maxQuantity,
               @Param("reorderLevel") BigDecimal reorderLevel, @Param("unitCost") BigDecimal unitCost,
               @Param("purchasePrice") BigDecimal purchasePrice, @Param("salePrice") BigDecimal salePrice,
               @Param("expiryDate") LocalDate expiryDate, @Param("batchNumber") String batchNumber,
               @Param("location") String location, @Param("supplierId") UUID supplierId,
               @Param("isTracked") Boolean isTracked, @Param("isActive") Boolean isActive,
               @Param("notes") String notes);

    // Bulk update stock quantities
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE InventoryItem i SET " +
            "i.currentQuantity = :newQuantity, " +
            "i.updatedAt = CURRENT_TIMESTAMP WHERE i.id = :id")
    int updateCurrentQuantity(@Param("id") UUID id, @Param("newQuantity") BigDecimal newQuantity);

    // Check if inventory item exists and is active
    Optional<InventoryItem> findByIdAndIsActive(UUID id, Boolean isActive);

    // Count items by category
    long countByCategory(String category);

    // Find items that need reordering (below reorder level)
    @Query("SELECT i FROM InventoryItem i WHERE COALESCE(i.reorderLevel, 0) > 0 AND " +
            "COALESCE(i.currentQuantity, 0) < i.reorderLevel")
    List<InventoryItem> findByIsActiveTrueAndReorderLevelGreaterThanCurrentQuantity();
}
