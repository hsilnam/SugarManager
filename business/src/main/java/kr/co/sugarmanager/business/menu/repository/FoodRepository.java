package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
    @Query("select sum(f.foodCal) from FoodEntity f where f.menuEntity.menuPk = :menuPk ")
    Integer caloriesPerMenu(@Param("menuPk") Long menuPk);
}
