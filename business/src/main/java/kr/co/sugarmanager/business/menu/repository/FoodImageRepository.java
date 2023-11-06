package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.FoodImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodImageRepository extends JpaRepository<FoodImageEntity, Long> {
}
