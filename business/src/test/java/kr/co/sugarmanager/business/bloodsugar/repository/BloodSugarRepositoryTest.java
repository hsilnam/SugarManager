package kr.co.sugarmanager.business.bloodsugar.repository;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.entity.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
                .cateory(BLOODSUGARCATEGORY.BEFORE)
                .level(200)
                .content("test")
                .build();
        //when
        BloodSugarEntity save = bloodSugarRepository.save(bloodSugarEntity);
        //then
        assertThat(save.getUserPk()).isEqualTo(1L);
    }
}
