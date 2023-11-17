package kr.co.sugarmanager.business.bloodsugar.repository;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarPeriodInterface;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BloodSugarRepository extends JpaRepository<BloodSugarEntity, Long> {
    Optional<BloodSugarEntity> findByBloodSugarPkAndUserPk(Long bloodSugarPk, Long userPk);

    @Query("SELECT e FROM BloodSugarEntity e WHERE e.userPk = :userPk AND YEAR(e.registedAt) = :year AND MONTH(e.registedAt) = :month AND DAY(e.registedAt) = :day")
    List<BloodSugarEntity> findByUserPkAndUpdatedAt(@Param("userPk") Long userPk, @Param("year") int year, @Param("month") int month, @Param("day") int day);

    @Query("SELECT " +
            "  DATE(e.registedAt) AS time, " +
            "  ROUND(AVG(CASE WHEN e.category = 'BEFORE' THEN e.level END), 1) AS bloodSugarBefore, " +
            "  ROUND(AVG(CASE WHEN e.category = 'AFTER' THEN e.level END), 1) AS bloodSugarAfter, " +
            "  COUNT(*) AS count, " +
            "  COALESCE(" +
            "    CASE " +
            "      WHEN ROUND(AVG(CASE WHEN e.category = 'BEFORE' THEN e.level END), 1) BETWEEN 70 AND 130 THEN 'SAFETY' " +
            "      WHEN ROUND(AVG(CASE WHEN e.category = 'BEFORE' THEN e.level END), 1) BETWEEN 63 AND 143 THEN 'WARNING' " +
            "      WHEN ROUND(AVG(CASE WHEN e.category = 'BEFORE' THEN e.level END), 1) BETWEEN 0 AND 1000 THEN 'DANGER' " +
            "    END, NULL) AS bloodSugarBeforeStatus, " +
            "  COALESCE(" +
            "    CASE " +
            "      WHEN ROUND(AVG(CASE WHEN e.category = 'AFTER' THEN e.level END), 1) BETWEEN 90 AND 180 THEN 'SAFETY' " +
            "      WHEN ROUND(AVG(CASE WHEN e.category = 'AFTER' THEN e.level END), 1) BETWEEN 81 AND 198 THEN 'WARNING' " +
            "      WHEN ROUND(AVG(CASE WHEN e.category = 'AFTER' THEN e.level END), 1) BETWEEN 0 AND 1000 THEN 'DANGER' " +
            "    END, NULL) AS bloodSugarAfterStatus " +
            "FROM BloodSugarEntity e " +
            "WHERE e.userPk = :userPk AND e.registedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(e.registedAt) " +
            "ORDER BY DATE(e.registedAt) DESC")
    Page<BloodSugarPeriodInterface> findByPeriod(@Param("userPk") Long userPk, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, PageRequest pageRequest);

    @Query("select distinct(cast (b.registedAt as localdate)) from BloodSugarEntity b where year(b.registedAt) = :year and month(b.registedAt) = :month and b.userPk = :searchUserPk")
    List<LocalDate> findBloodSugarRecordsForMonth(@Param("searchUserPk") Long searchUserPk, @Param("year") Integer year, @Param("month") Integer month);

    @Query("select b from BloodSugarEntity b where b.userPk = :searchUserPk and b.registedAt between :start and :end" )
    List<BloodSugarEntity> findBloodSugarRecordsForDay(@Param("searchUserPk") Long searchUserPk, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT e FROM BloodSugarEntity e WHERE e.userPk = :userPk AND e.category = :category AND e.registedAt BETWEEN :startDate AND :endDate ORDER BY e.registedAt DESC LIMIT 1")
    Optional<BloodSugarEntity> findOneByUserPkAndCategoryAndRegistedAt(
            @Param("userPk") Long userPk,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
            );
}