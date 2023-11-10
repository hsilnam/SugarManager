package kr.co.sugarmanager.business.menu.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.menu.dto.FoodDTO;
import kr.co.sugarmanager.business.menu.dto.MenuDeleteDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSaveDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MenuServiceTest {
    @Autowired
    private MenuService menuService;

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
        // then
    }
}
