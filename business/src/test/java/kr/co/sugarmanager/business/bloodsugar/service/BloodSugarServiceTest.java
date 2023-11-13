package kr.co.sugarmanager.business.bloodsugar.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.dto.*;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.exception.BloodSugarException;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BloodSugarServiceTest {
    @Autowired
    private BloodSugarService bloodSugarService;

    @Autowired
    private BloodSugarRepository bloodSugarRepository;
    @Autowired
    private UserRepository userRepository;

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
            assertFalse(response.isSuccess());
        });
    }

    @Test
    void 삭제_성공() throws Exception {
        //given
        BloodSugarDeleteDTO.Request request = BloodSugarDeleteDTO.Request.builder()
                .bloodSugarPk(bloodSugarEntity.getBloodSugarPk())
                .build();

        //when
        BloodSugarDeleteDTO.Response response = bloodSugarService.delete(1L, request);

        assertTrue(response.isSuccess());
    }

    @Test
    void 삭제_실패() throws Exception {
        //given
        BloodSugarDeleteDTO.Request request = BloodSugarDeleteDTO.Request.builder()
                .bloodSugarPk(bloodSugarEntity.getBloodSugarPk())
                .build();

        //when
        assertThrows(BloodSugarException.class, () -> {
            BloodSugarDeleteDTO.Response response = bloodSugarService.delete(2L, request);
            assertFalse(response.isSuccess());
        });
    }

    @Test
    void 조회_성공() throws Exception {
        //given
        String userNickName = userRepository.findNicknameById(1L);
        //when
        BloodSugarSelectDTO.Response response = bloodSugarService.select(1L, userNickName, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth());

        System.out.println(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void 조회_실패() throws Exception {
        //given
        String userNickName = userRepository.findNicknameById(1L);

        //when
        BloodSugarSelectDTO.Response response = bloodSugarService.select(1L, userNickName, LocalDateTime.now().getYear()+1, LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth());
        assertTrue(response.isSuccess());
        assertEquals(response.getResponse().getBloodSugarMax(), 0);
    }
}
