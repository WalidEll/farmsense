package ma.farmsense.repository;

import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CropRepository extends JpaRepository<Crop, UUID> {

    List<Crop> findByCategory(CropCategory category);

    @Query("SELECT c FROM Crop c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.scientificName) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Crop> search(@Param("q") String query);

    @Query("SELECT c FROM Crop c WHERE c.category = :cat AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.scientificName) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Crop> searchByCategory(@Param("cat") CropCategory category, @Param("q") String query);
}
