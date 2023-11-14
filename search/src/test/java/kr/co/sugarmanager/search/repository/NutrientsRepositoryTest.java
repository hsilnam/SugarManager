package kr.co.sugarmanager.search.repository;

import kr.co.sugarmanager.search.entity.NutrientsEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NutrientsRepositoryTest {
    @Autowired
    private NutrientsRepository nutrientsRepository;

    @Test
    public void 음식_이름으로_검색하기_성공(){
        //given
        String name="김";
        //when
        List<NutrientsEntity> find = nutrientsRepository.findTop20ByNutrientsNameContaining(name);
        //then
        assertTrue(find.size() > 0);
    }
}
