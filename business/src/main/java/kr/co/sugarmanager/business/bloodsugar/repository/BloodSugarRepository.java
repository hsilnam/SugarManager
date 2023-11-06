package kr.co.sugarmanager.business.bloodsugar.repository;

import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodSugarRepository extends JpaRepository<BloodSugarEntity, Long> {
}