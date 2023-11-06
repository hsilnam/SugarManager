package kr.co.sugarmanager.image.repository;

import kr.co.sugarmanager.image.entity.FoodImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodImageRepository extends JpaRepository<FoodImageEntity, Long> {
}
