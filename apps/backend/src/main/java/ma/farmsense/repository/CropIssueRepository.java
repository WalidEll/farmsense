package ma.farmsense.repository;

import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropIssue;
import ma.farmsense.entity.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CropIssueRepository extends JpaRepository<CropIssue, UUID> {
    List<CropIssue> findByCrop(Crop crop);
    List<CropIssue> findByCropAndIssueType(Crop crop, IssueType issueType);
    void deleteByCrop(Crop crop);
}
