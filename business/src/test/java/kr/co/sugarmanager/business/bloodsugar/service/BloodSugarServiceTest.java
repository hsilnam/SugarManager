package kr.co.sugarmanager.business.bloodsugar.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.entity.BLOODSUGARCATEGORY;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BloodSugarServiceTest {
    @Autowired
    private BloodSugarService bloodSugarService;

    @Test
    @Transactional
    void 등록_성공() throws Exception {
        //given
        BloodSugarSaveDTO.Request request = BloodSugarSaveDTO.Request.builder()
                .cateory(BLOODSUGARCATEGORY.BEFORE)
                .level(400)
                .content("test")
                .build();

        //when
        BloodSugarSaveDTO.Response response = bloodSugarService.save(1L, request);
        //then
        assertTrue(response.isSuccess());
    }

}
