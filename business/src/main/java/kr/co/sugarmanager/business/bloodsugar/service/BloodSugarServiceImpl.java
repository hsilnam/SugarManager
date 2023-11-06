package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarUpdateDTO;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.exception.BloodSugarException;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BloodSugarServiceImpl implements BloodSugarService{
    private final BloodSugarRepository bloodSugarRepository;

    @Override
    public BloodSugarSaveDTO.Response save(Long userPk, BloodSugarSaveDTO.Request request) {
        BloodSugarEntity bloodSugar = BloodSugarEntity.builder()
                .userPk(userPk)
                .category(request.getCategory().name())
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

    @Override
    public BloodSugarUpdateDTO.Response update(Long userPk, BloodSugarUpdateDTO.Request request) {
        BloodSugarEntity bloodSugar = bloodSugarRepository.findByBloodSugarPkAndUserPk(request.getBloodSugarPk(), userPk);
        try {
            if (bloodSugar == null) {
                throw new BloodSugarException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
            log.info(bloodSugar.toString());
            bloodSugar.setLevel(request.getLevel());
            bloodSugar.setCategory(request.getCategory().name());
            bloodSugar.setContent(request.getContent());
            return BloodSugarUpdateDTO.Response.builder()
                    .success(true)
                    .response(null)
                    .error(null)
                    .build();
        } catch (IllegalArgumentException | ValidationException ex) {
            throw new BloodSugarException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
