package kr.co.sugarmanager.business.bloodsugar.repository;

import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BloodSugarRepository extends JpaRepository<BloodSugarEntity, Long> {
    @Query("SELECT s FROM BloodSugarEntity s WHERE s.bloodSugarPk = :bloodSugarPk and s.userPk = :userPk")
    BloodSugarEntity findByBloodSugarPkAndUserPk(@Param("bloodSugarPk") Long bloodSugarPk, @Param("userPk") Long userPk);
}