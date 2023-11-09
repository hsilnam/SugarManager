package kr.co.sugarmanager.business.bloodsugar.repository;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.dto.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class BloodSugarRepositoryTest {
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
    @Transactional
    void 혈당_저장_성공() throws Exception {
        //given
        BloodSugarEntity bloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BLOODSUGARCATEGORY.BEFORE.name())
                .level(200)
                .content("test")
                .build();
        //when
        BloodSugarEntity save = bloodSugarRepository.save(bloodSugarEntity);
        //then
        assertThat(save.getUserPk()).isEqualTo(1L);
    }

    @Test
    @Transactional
    void 혈당_수정_성공() throws Exception {
        //given
        BloodSugarEntity bloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BLOODSUGARCATEGORY.BEFORE.name())
                .level(200)
                .content("test")
                .build();
        BloodSugarEntity save = bloodSugarRepository.save(bloodSugarEntity);

        Optional<BloodSugarEntity> optionalBloodSugarEntity = bloodSugarRepository.findByBloodSugarPkAndUserPk(save.getBloodSugarPk(), 1L);
        //when
        BloodSugarEntity bloodSugar = optionalBloodSugarEntity.get();
        bloodSugar.setCategory(BLOODSUGARCATEGORY.AFTER.name());
        bloodSugar.setLevel(100);
        bloodSugar.setContent("test!");
        BloodSugarEntity expect = bloodSugarRepository.save(bloodSugar);

        //then
        assertThat(expect.getLevel()).isEqualTo(100);
    }

    @Test
    @Transactional
    void 혈당_수정_실패() throws Exception {
        //given
        BloodSugarEntity bloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BLOODSUGARCATEGORY.BEFORE.name())
                .level(200)
                .content("test")
                .build();
        BloodSugarEntity save = bloodSugarRepository.save(bloodSugarEntity);

        //when
        Optional<BloodSugarEntity> optionalBloodSugarEntity = bloodSugarRepository.findByBloodSugarPkAndUserPk(save.getBloodSugarPk(), 3L);

        assertFalse(optionalBloodSugarEntity.isPresent());
    }

    @Test
    void 혈당_조회() throws Exception {
        //given

        //when
        List<BloodSugarEntity> result = bloodSugarRepository.findByUserPkAndUpdatedAt(
                1L,
                bloodSugarEntity.getCreatedAt().getYear(),
                bloodSugarEntity.getCreatedAt().getMonthValue(),
                bloodSugarEntity.getCreatedAt().getDayOfMonth());
        //then
        assertTrue(result.size() > 0);
    }
}