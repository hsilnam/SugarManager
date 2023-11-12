package kr.co.sugarmanager.business.bloodsugar.repository;
;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BloodSugarRepository extends JpaRepository<BloodSugarEntity, Long> {
    Optional<BloodSugarEntity> findByBloodSugarPkAndUserPk(Long bloodSugarPk, Long userPk);

    @Query("SELECT e FROM BloodSugarEntity e WHERE e.userPk = :userPk AND YEAR(e.updatedAt) = :year AND MONTH(e.updatedAt) = :month AND DAY(e.updatedAt) = :day")
    List<BloodSugarEntity> findByUserPkAndUpdatedAt(@Param("userPk") Long userPk, @Param("year") int year, @Param("month") int month, @Param("day") int day);

    @Query("SELECT e FROM BloodSugarEntity e WHERE e.userPk = :userPk AND e.category = :category AND e.createdAt BETWEEN :startDate AND :endDate ORDER BY e.createdAt DESC LIMIT 1")
    Optional<BloodSugarEntity> findOneByUserPkAndCategoryAndCreatedAt(
            @Param("userPk") Long userPk,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
            );
}