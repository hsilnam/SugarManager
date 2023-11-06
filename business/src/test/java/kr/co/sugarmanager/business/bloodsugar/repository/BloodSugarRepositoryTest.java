package kr.co.sugarmanager.business.bloodsugar.repository;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BloodSugarRepositoryTest {
    @Autowired
    private BloodSugarRepository bloodSugarRepository;

    @Test
    @Transactional
    void 혈당_저장_성공() throws Exception {
        //given
        BloodSugarEntity bloodSugarEntity = BloodSugarEntity.builder()
                .userPk(1L)
                .category(BloodSugarSaveDTO.BLOODSUGARCATEGORY.BEFORE.name())
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
                .category(BloodSugarSaveDTO.BLOODSUGARCATEGORY.BEFORE.name())
                .level(200)
                .content("test")
                .build();
        BloodSugarEntity save = bloodSugarRepository.save(bloodSugarEntity);

        BloodSugarEntity bloodSugar = bloodSugarRepository.findByBloodSugarPkAndUserPk(save.getBloodSugarPk(), 1L);
        //when
        bloodSugar.setCategory(BloodSugarSaveDTO.BLOODSUGARCATEGORY.AFTER.name());
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
                .category(BloodSugarSaveDTO.BLOODSUGARCATEGORY.BEFORE.name())
                .level(200)
                .content("test")
                .build();
        BloodSugarEntity save = bloodSugarRepository.save(bloodSugarEntity);

        //when
        assertThrows(NullPointerException.class, () -> {
            BloodSugarEntity bloodSugar = bloodSugarRepository.findByBloodSugarPkAndUserPk(save.getBloodSugarPk(), 2L);
            if (bloodSugar == null) throw new NullPointerException();
        });
    }
}
