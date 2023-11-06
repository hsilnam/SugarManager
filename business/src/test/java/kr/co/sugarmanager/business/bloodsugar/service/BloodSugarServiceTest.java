package kr.co.sugarmanager.business.bloodsugar.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.dto.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarUpdateDTO;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.exception.BloodSugarException;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class BloodSugarServiceTest {
    @Autowired
    private BloodSugarService bloodSugarService;

    @Autowired
    private BloodSugarRepository bloodSugarRepository;

    private BloodSugarEntity bloodSugarEntity;

    @BeforeEach
    void init() throws Exception {
        bloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BLOODSUGARCATEGORY.BEFORE.name())
                .level(200)
                .content("test")
                .build();

        //when
        bloodSugarEntity = bloodSugarRepository.save(bloodSugarEntity);
    }

    @Test
    void 등록_성공() throws Exception {
        //given
        BloodSugarSaveDTO.Request request = BloodSugarSaveDTO.Request.builder()
                .category(BLOODSUGARCATEGORY.BEFORE)
                .level(400)
                .content("test")
                .build();

        //when
        BloodSugarSaveDTO.Response response = bloodSugarService.save(1L, request);
        //then
        assertTrue(response.isSuccess());
    }

    @Test
    void 수정_성공() throws Exception {
        //given
        BloodSugarUpdateDTO.Request request = BloodSugarUpdateDTO.Request.builder()
                .bloodSugarPk(bloodSugarEntity.getBloodSugarPk())
                .category(BLOODSUGARCATEGORY.BEFORE)
                .level(400)
                .content("test")
                .build();

        //when
        BloodSugarUpdateDTO.Response response = bloodSugarService.update(1L, request);
        //then
        assertTrue(response.isSuccess());
    }

    @Test
    void 수정_실패() throws Exception {
        //given
        BloodSugarUpdateDTO.Request request = BloodSugarUpdateDTO.Request.builder()
                .bloodSugarPk(bloodSugarEntity.getBloodSugarPk())
                .category(BLOODSUGARCATEGORY.BEFORE)
                .level(400)
                .content("test")
                .build();

        //when
        assertThrows(BloodSugarException.class, () -> {
            BloodSugarUpdateDTO.Response response = bloodSugarService.update(2L, request);
        });
    }
}
