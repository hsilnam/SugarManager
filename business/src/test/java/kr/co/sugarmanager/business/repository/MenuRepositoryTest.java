package kr.co.sugarmanager.business.repository;

import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class MenuRepositoryTest {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FoodRepository foodRepository;

    @Test
    void 식단_등록_성공() throws Exception {
        //given
        MenuEntity menuEntity = MenuEntity.builder()
                .userPk(1L)
                .foodList(new ArrayList<>())
                .foodImageList(new ArrayList<>())
                .build();
        //when
        MenuEntity saveMenu = menuRepository.save(menuEntity);
        FoodEntity foodEntity = FoodEntity.builder()
                .menuPk(menuEntity.getMenuPk())
                .foodName("test")
                .foodCarbohydrate(13)
                .foodProtein(4.3F)
                .build();
        menuEntity.addFoodEntity(foodEntity);
        FoodEntity saveFood = foodRepository.save(foodEntity);

        //then
        assertEquals(saveMenu.getUserPk(), menuEntity.getUserPk());
        assertEquals(saveFood.getMenuPk(), foodEntity.getMenuPk());
    }
}
