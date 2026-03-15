package ma.farmsense.repository;

import ma.farmsense.entity.Breed;
import ma.farmsense.entity.BreedCategory;
import ma.farmsense.entity.BreedPurpose;
import ma.farmsense.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BreedRepository extends JpaRepository<Breed, UUID> {

    @Query("SELECT b FROM Breed b WHERE b.isSystem = true OR b.user = :user ORDER BY b.name ASC")
    Page<Breed> findByIsSystemTrueOrUserOrderByNameAsc(@Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.descriptionAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.descriptionEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Breed> search(@Param("search") String search, @Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND b.category = :category")
    Page<Breed> findByCategory(@Param("category") BreedCategory category, @Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND b.purpose = :purpose")
    Page<Breed> findByPurpose(@Param("purpose") BreedPurpose purpose, @Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND " +
           "b.category = :category AND b.purpose = :purpose")
    Page<Breed> findByCategoryAndPurpose(@Param("category") BreedCategory category, 
                                         @Param("purpose") BreedPurpose purpose, 
                                         @Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :search, '%'))) AND b.category = :category")
    Page<Breed> searchByCategory(@Param("search") String search, @Param("category") BreedCategory category, 
                                 @Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :search, '%'))) AND b.purpose = :purpose")
    Page<Breed> searchByPurpose(@Param("search") String search, @Param("purpose") BreedPurpose purpose, 
                               @Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Breed b WHERE (b.isSystem = true OR b.user = :user) AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :search, '%'))) AND b.category = :category AND b.purpose = :purpose")
    Page<Breed> searchByCategoryAndPurpose(@Param("search") String search, @Param("category") BreedCategory category,
                                           @Param("purpose") BreedPurpose purpose, @Param("user") User user, Pageable pageable);

    long countByUserAndNameIgnoreCase(User user, String name);

    Optional<Breed> findByIsSystemTrueAndNameIgnoreCase(String name);

    Optional<Breed> findByIdAndUser(UUID id, User user);

    @Query("SELECT b FROM Breed b WHERE b.id = :id AND (b.isSystem = true OR b.user = :user)")
    Optional<Breed> findByIdAndVisible(@Param("id") UUID id, @Param("user") User user);

    @Query("SELECT b FROM Breed b WHERE LOWER(b.name) = LOWER(:name)")
    Optional<Breed> findByNameIgnoreCase(@Param("name") String name);
}
