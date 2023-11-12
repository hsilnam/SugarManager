package kr.co.sugarmanager.business.bloodsugar.repository;

import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BloodSugarRepository extends JpaRepository<BloodSugarEntity, Long> {
    Optional<BloodSugarEntity> findByBloodSugarPkAndUserPk(Long bloodSugarPk, Long userPk);

    @Query("SELECT e FROM BloodSugarEntity e WHERE e.userPk = :userPk AND YEAR(e.updatedAt) = :year AND MONTH(e.updatedAt) = :month AND DAY(e.updatedAt) = :day")
    List<BloodSugarEntity> findByUserPkAndUpdatedAt(@Param("userPk") Long userPk, @Param("year") int year, @Param("month") int month, @Param("day") int day);

    @Query("select distinct(cast (b.createdAt as localdate)) from BloodSugarEntity b where year(b.createdAt) = :year and month(b.createdAt) = :month and b.userPk = :searchUserPk")
    List<LocalDate> findBloodSugarRecordsForMonth(@Param("searchUserPk") Long searchUserPk, @Param("year") Integer year, @Param("month") Integer month);

    @Query("select b from BloodSugarEntity b where b.userPk = :searchUserPk and b.createdAt between :start and :end" )
    List<BloodSugarEntity> findBloodSugarRecordsForDay(@Param("searchUserPk") Long searchUserPk, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}