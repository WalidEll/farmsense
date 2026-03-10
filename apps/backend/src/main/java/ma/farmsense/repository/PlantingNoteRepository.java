package ma.farmsense.repository;

import ma.farmsense.entity.Planting;
import ma.farmsense.entity.PlantingNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlantingNoteRepository extends JpaRepository<PlantingNote, UUID> {

    List<PlantingNote> findByPlantingOrderByCreatedAtDesc(Planting planting);
}
