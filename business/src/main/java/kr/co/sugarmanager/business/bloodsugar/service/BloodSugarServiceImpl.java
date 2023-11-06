package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarDeleteDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSelectDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarUpdateDTO;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.exception.BloodSugarException;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<BloodSugarEntity> optionalBloodSugarEntity = bloodSugarRepository.findByBloodSugarPkAndUserPk(request.getBloodSugarPk(), userPk);
        try {
            if (!optionalBloodSugarEntity.isPresent()) {
                throw new BloodSugarException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
            BloodSugarEntity bloodSugar = optionalBloodSugarEntity.get();
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

    @Override
    public BloodSugarDeleteDTO.Response delete(Long userPk, BloodSugarDeleteDTO.Request request) {
        Optional<BloodSugarEntity> optionalBloodSugarEntity = bloodSugarRepository.findByBloodSugarPkAndUserPk(request.getBloodSugarPk(), userPk);
        if (!optionalBloodSugarEntity.isPresent()) {
            throw new BloodSugarException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        bloodSugarRepository.delete(optionalBloodSugarEntity.get());

        return BloodSugarDeleteDTO.Response.builder()
                .success(true)
                .response(null)
                .error(null)
                .build();
    }

    @Override
    public BloodSugarSelectDTO.Response select(Long userPk, int year, int month, int day) {
        List<BloodSugarEntity> selectResult = bloodSugarRepository.findByUserPkAndUpdatedAt(userPk, year, month, day);
        int minBloodSugar = 501;
        int maxBloodSugar = -1;
        BloodSugarSelectDTO.Response returnDTO = BloodSugarSelectDTO.Response.builder()
                .success(true)
                .response(BloodSugarSelectDTO.ReturnResponse.builder().build())
                .error(null)
                .build();
        returnDTO.getResponse().setList(new ArrayList<>());
        for (BloodSugarEntity bloodSugarEntity: selectResult) {
            minBloodSugar = Math.min(minBloodSugar, bloodSugarEntity.getLevel());
            maxBloodSugar = Math.max(maxBloodSugar, bloodSugarEntity.getLevel());

            returnDTO.getResponse().addList(BloodSugarSelectDTO.EntityResponse.builder()
                            .bloodSugarPk(bloodSugarEntity.getBloodSugarPk())
                            .category(bloodSugarEntity.getCategory())
                            .content(bloodSugarEntity.getContent())
                            .updatedAt(bloodSugarEntity.getUpdatedAt())
                            .level(bloodSugarEntity.getLevel())
                    .build());
        }

        returnDTO.getResponse().setBloodSugarMax(maxBloodSugar == -1 ? 0 : maxBloodSugar);
        returnDTO.getResponse().setBloodSugarMin(minBloodSugar == 501 ? 0: minBloodSugar);

        return returnDTO;
    }
}
