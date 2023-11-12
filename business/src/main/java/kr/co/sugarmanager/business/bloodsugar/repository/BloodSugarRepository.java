package kr.co.sugarmanager.business.bloodsugar.repository;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarPeriodInterface;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Query("SELECT DATE(e.createdAt) AS time, ROUND(AVG(CASE WHEN e.category = 'BEFORE' THEN e.level END), 1) AS bloodSugarBefore, ROUND(AVG(CASE WHEN e.category = 'AFTER' THEN e.level END), 1) AS bloodSugarAfter, COUNT(*) AS count FROM BloodSugarEntity e WHERE e.userPk = :userPk AND e.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(e.createdAt) order by DATE(e.createdAt) desc")
    Page<BloodSugarPeriodInterface> findByPeriod(@Param("userPk") Long userPk, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, PageRequest pageRequest);
}