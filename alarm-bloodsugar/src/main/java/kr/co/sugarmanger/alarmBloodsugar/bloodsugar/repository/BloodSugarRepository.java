package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository;

import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BloodSugarRepository extends JpaRepository<BloodSugarEntity,Long> {

    @Query("select b from BloodSugarEntity b where b.created_at between :start and :end and b.category = 'AFTER'")
    BloodSugarEntity checkRecord(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);

}
