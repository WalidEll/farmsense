package ma.farmsense.repository;

import ma.farmsense.entity.Flock;
import ma.farmsense.entity.MortalityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MortalityRecordRepository extends JpaRepository<MortalityRecord, UUID> {

    List<MortalityRecord> findByFlockOrderByMortalityDateDesc(Flock flock);
}
