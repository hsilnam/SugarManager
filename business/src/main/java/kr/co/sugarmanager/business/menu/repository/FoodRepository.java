package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
}
