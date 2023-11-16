package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.*;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.exception.BloodSugarException;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.global.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BloodSugarServiceImpl implements BloodSugarService{
    private final BloodSugarRepository bloodSugarRepository;
    private final UserRepository userRepository;
    @Override
    public BloodSugarSaveDTO.Response save(Long userPk, BloodSugarSaveDTO.Request request) {
        try {
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
        } catch (NullPointerException e) {
            throw new BloodSugarException(ErrorCode.MISSING_INPUT_VALUE);
        }
    }

    @Override
    public BloodSugarUpdateDTO.Response update(Long userPk, BloodSugarUpdateDTO.Request request) {
        Optional<BloodSugarEntity> optionalBloodSugarEntity = bloodSugarRepository.findByBloodSugarPkAndUserPk(request.getBloodSugarPk(), userPk);
        try {
            if (optionalBloodSugarEntity.isEmpty()) {
                throw new BloodSugarException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
            BloodSugarEntity bloodSugar = optionalBloodSugarEntity.get();
            bloodSugar.setLevel(request.getLevel());
            bloodSugar.setCategory(request.getCategory().name());
            bloodSugar.setContent(request.getContent());
            bloodSugarRepository.save(bloodSugar);
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
        if (optionalBloodSugarEntity.isEmpty()) {
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
    public BloodSugarSelectDTO.Response select(Long userPk, String targetUserNickname, int year, int month, int day) {
        Long targetUserPk = isSameGroup(userPk, targetUserNickname);

        List<BloodSugarEntity> selectResult = bloodSugarRepository.findByUserPkAndUpdatedAt(targetUserPk, year, month, day);
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
                    .createdAt(bloodSugarEntity.getCreatedAt())
                    .level(bloodSugarEntity.getLevel())
                    .status(getSugarBloodStatus(BLOODSUGARCATEGORY.valueOf(bloodSugarEntity.getCategory()), bloodSugarEntity.getLevel()))
                    .build());
        }


        returnDTO.getResponse().setBloodSugarMax(maxBloodSugar == -1 ? 0 : maxBloodSugar);
        returnDTO.getResponse().setBloodSugarMin(minBloodSugar == 501 ? 0: minBloodSugar);

        return returnDTO;
    }

    @Override
    public BloodSugarPeriodDTO.Response selectPeriod(Long userPk, String targetUserNickname, String startDate, String endDate, int page) {
        Long targetUserPk = isSameGroup(userPk, targetUserNickname);

        PageRequest pageRequest = PageRequest.of(page, 30);
        LocalDateTime startLocalDate = convertStringToLocalDateTime(startDate);
        LocalDateTime endLocalDate = convertStringToLocalDateTime(endDate).plusDays(1L);

        if (startLocalDate.isAfter(endLocalDate)) {
            throw new BloodSugarException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return BloodSugarPeriodDTO.Response.builder()
                .success(true)
                .response(bloodSugarRepository.findByPeriod(targetUserPk, startLocalDate, endLocalDate, pageRequest).getContent())
                .error(null)
                .build();
    }

    private LocalDateTime convertStringToLocalDateTime(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay();
        } catch (RuntimeException e) {
            throw new BloodSugarException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Long isSameGroup(Long userPk, String targetUserNickname) {
        Long targetUserId = userRepository.findIdByNickname(targetUserNickname)
                .orElseThrow(() -> new ValidationException(ErrorCode.NO_SUCH_USER));
        if (!targetUserId.equals(userPk)
                && !userRepository.inSameGroup(userPk, targetUserNickname)) {
            throw new BloodSugarException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        return targetUserId;
    }

    private SUGARBLOODSTATUS getSugarBloodStatus(BLOODSUGARCATEGORY category, int level) {
        switch (category) {
            case BEFORE -> {
                if (level >= 70 && level <= 130) return SUGARBLOODSTATUS.SAFETY;
                else if (level >= 63 && level < 143) return SUGARBLOODSTATUS.WARNING;
                else if (level >= 0 && level < 1000) return SUGARBLOODSTATUS.DANGER;
            }
            case AFTER -> {
                if (level >= 90 && level <= 180) return SUGARBLOODSTATUS.SAFETY;
                else if (level >= 81 && level < 198) return SUGARBLOODSTATUS.WARNING;
                else if (level >= 0 && level < 1000) return SUGARBLOODSTATUS.DANGER;
            }
        }
        return null;
    }
}
