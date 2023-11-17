package kr.co.sugarmanager.business.menu.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.dto.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.menu.dto.FoodDTO;
import kr.co.sugarmanager.business.menu.dto.MenuDeleteDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSaveDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSelectDTO;
import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class MenuServiceTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private BloodSugarRepository bloodSugarRepository;

    private BloodSugarEntity saveBeforeBloodSugar;
    private BloodSugarEntity saveAfterBloodSugar;
    private MenuEntity saveMenu;
    private FoodEntity saveFood;

    @BeforeEach
    void init() throws Exception {
        BloodSugarEntity beforeBloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BLOODSUGARCATEGORY.BEFORE.name())
                .level(100)
                .content("test")
                .build();
        saveBeforeBloodSugar = bloodSugarRepository.save(beforeBloodSugarEntity);

        MenuEntity menuEntity = MenuEntity.builder()
                .userPk(1L)
                .foodList(new ArrayList<>())
                .foodImageList(new ArrayList<>())
                .build();
        saveMenu = menuRepository.save(menuEntity);
        FoodEntity foodEntity = FoodEntity.builder()
                .menuEntity(saveMenu)
                .foodName("test")
                .foodCarbohydrate(13)
                .foodProtein(4.3F)
                .build();
        saveMenu.addFoodEntity(foodEntity);
        saveFood = foodRepository.save(foodEntity);

        BloodSugarEntity afterBloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BLOODSUGARCATEGORY.AFTER.name())
                .level(200)
                .content("test")
                .build();
        saveAfterBloodSugar = bloodSugarRepository.save(afterBloodSugarEntity);
    }
    @Test
    @Transactional
    void 메뉴_저장_성공() throws Exception {
        //given
        FoodDTO foodDTO = FoodDTO.builder()
                .foodName("test")
                .build();
        List<FoodDTO> foodDTOList = new ArrayList<>();
        foodDTOList.add(foodDTO);

        MenuSaveDTO.Request request = MenuSaveDTO.Request.builder()
                .foods(foodDTOList)
                .build();

        //when
        MenuSaveDTO.Response save = menuService.save(1L, null, request);

        //then
        assertEquals(save.isSuccess(), true);
    }

    @Test
    @Transactional
    void 식단_삭제_성공() throws Exception {
        //given
        MenuDeleteDTO.Request menuDeleteDTO = MenuDeleteDTO.Request.builder().menuPk("1").build();
        //when
        MenuDeleteDTO.Response delete = menuService.delete(1L, menuDeleteDTO);
        //then
        assertEquals(delete.isSuccess(), true);
    }

    @Test
    @Transactional
    void 식단_조회_성공() throws Exception {
        // given
        // when
        MenuSelectDTO.Request req = MenuSelectDTO.Request.builder()
                .userPk(1L)
                .menuPk(saveMenu.getMenuPk())
                .build();
        MenuSelectDTO.Response select = menuService.select(req);

        // then
        assertEquals(true, true);
        assertAll("select sucess",
                () -> assertEquals(select.isSuccess(), true),
                () -> assertEquals(select.getResponse().getMenuPk(), saveMenu.getMenuPk())
//                () -> assertEquals(select.getResponse().getBloodSugar().getBeforeLevel(), Integer.valueOf(saveBeforeBloodSugar.getLevel())),
//                () -> assertEquals(select.getResponse().getBloodSugar().getAfterLevel(), Integer.valueOf(saveAfterBloodSugar.getLevel()))
        ); // TODO: 나중에 더 추가할 것
    }
}
