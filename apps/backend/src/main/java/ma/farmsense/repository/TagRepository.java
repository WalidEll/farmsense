package ma.farmsense.repository;

import ma.farmsense.entity.Tag;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    List<Tag> findByUserOrderByNameAsc(User user);
    Optional<Tag> findByUserAndName(User user, String name);
}
