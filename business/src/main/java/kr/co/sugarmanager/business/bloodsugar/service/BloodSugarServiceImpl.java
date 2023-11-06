package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BloodSugarServiceImpl implements BloodSugarService{
    private final BloodSugarRepository bloodSugarRepository;

    @Override
    public BloodSugarSaveDTO.Response save(Long userPk, BloodSugarSaveDTO.Request request) {
        BloodSugarEntity bloodSugar = BloodSugarEntity.builder()
                .userPk(userPk)
                .cateory(request.getCateory())
                .level(request.getLevel())
                .content(request.getContent())
                .build();
        bloodSugarRepository.save(bloodSugar);
        return BloodSugarSaveDTO.Response.builder()
                .success(true)
                .response(null)
                .error(null)
                .build();
    }
}
