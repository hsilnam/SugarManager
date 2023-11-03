package kr.co.sugarmanager.business.repository;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MenuRepositoryTest {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FoodRepository foodRepository;

    @Test
    @Transactional
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
                .menuEntity(saveMenu)
                .foodName("test")
                .foodCarbohydrate(13)
                .foodProtein(4.3F)
                .build();
        menuEntity.addFoodEntity(foodEntity);
        FoodEntity saveFood = foodRepository.save(foodEntity);

        //then
        assertEquals(saveMenu.getUserPk(), menuEntity.getUserPk());
        assertEquals(saveFood.getFoodName(), foodEntity.getFoodName());
    }

    @Test
    @Transactional
    void 식단_삭제_권한_성공() throws Exception {
        //given
        Long menuPk = 11L;
        Long userPk = 1L;

        //when
        Optional<MenuEntity> menu = menuRepository.findByMenuPkAndUserPk(menuPk, userPk);
        //then
        assertEquals(menu.isPresent(), true);
    }

    @Test
    @Transactional
    void 식단_삭제_권한_실패() throws Exception {
        //given
        Long menuPk = 11L;
        Long userPk = 0L;

        //when
        Optional<MenuEntity> menu = menuRepository.findByMenuPkAndUserPk(menuPk, userPk);

        //then
        assertEquals(menu.isPresent(), false);
    }
}
