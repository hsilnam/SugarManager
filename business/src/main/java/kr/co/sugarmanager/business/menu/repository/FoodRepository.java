package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
    @Query(value = "SELECT * FROM FOOD WHERE MENU_PK = :menuPk AND FOOD_PK = :foodPk", nativeQuery = true)
    Optional<FoodEntity> findByMenuPkAndFoodPk(@Param("menuPk") Long menuPk, @Param("foodPk") Long foodPk);

    @Modifying
    @Query(value = "DELETE FROM FOOD WHERE FOOD_PK IN :foodPks AND MENU_PK = :menuPk", nativeQuery = true)
    void deleteByFoodPkInAndMenuPK(@Param("foodPks") List<Long> foodPks, @Param("menuPk") Long menuPk);

    @Query("select sum(f.foodCal) from FoodEntity f where f.menuEntity.menuPk = :menuPk ")
    Integer caloriesPerMenu(@Param("menuPk") Long menuPk);
}
