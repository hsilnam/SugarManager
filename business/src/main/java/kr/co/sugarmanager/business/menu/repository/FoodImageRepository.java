package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.FoodImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodImageRepository extends JpaRepository<FoodImageEntity, Long> {
    @Query(value = "SELECT * FROM FOOD_IMAGE WHERE MENU_PK = :menuPk AND FOOD_IMAGE_PK = :foodImagePk", nativeQuery = true)
    Optional<FoodImageEntity> findByMenuPkAndFoodImagePk(@Param("menuPk") Long menuPk, @Param("foodImagePk") Long foodImagePk);

    @Modifying
    @Query(value = "DELETE FROM FOOD_IMAGE WHERE FOOD_IMAGE_PK IN :foodImagePks AND MENU_PK = :menuPk", nativeQuery = true)
    void deleteByFoodImagePkInAndMenuPK(@Param("foodImagePks") List<Long> foodImagePks, @Param("menuPk") Long menuPk);

}
