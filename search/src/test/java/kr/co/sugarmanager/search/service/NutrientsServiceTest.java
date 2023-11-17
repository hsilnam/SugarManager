package kr.co.sugarmanager.search.service;

import kr.co.sugarmanager.search.dto.NutrientsFindDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NutrientsServiceTest {
    @Autowired
    private NutrientsService nutrientsService;

    @Test
    public void 메뉴검색성공(){
        //given
        String name = "김";
        //when
        NutrientsFindDTO.Response findResult = nutrientsService.find(name);
        //then
        assertTrue(findResult.isSuccess());
        assertTrue(findResult.getResponse().size() > 0);
    }

}
