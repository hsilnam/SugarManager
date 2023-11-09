package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.*;
import kr.co.sugarmanager.business.challenge.entity.ChallengeLogEntity;
import kr.co.sugarmanager.business.challenge.entity.ChallengeTemplateEntity;
import kr.co.sugarmanager.business.challenge.repository.ChallengeLogRepository;
import kr.co.sugarmanager.business.challenge.repository.ChallengeTemplateRepository;
import kr.co.sugarmanager.business.global.exception.CustomException;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.support.ConvertingPropertyEditorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeLogRepository challengeLogRepository;

    // 현욱이가 만들어주면 삭제
//    private final UserRepository userRepository;
//    private final SettingsRepository settingsRepository;

    // 오늘의 챌린지 모두 가져오기
    @Transactional(readOnly = true)
    @Override
    public TodayChallengesDTO.Response todaysChallenges() {

        int day = 1 << (LocalDateTime.now().getDayOfWeek().getValue() - 1);
        List<ChallengeTemplateEntity> challenges = challengeTemplateRepository.findTodaysChallenges(day);
//        log.info("size : {} , challenges : {}", challenges.size(), challenges);

        List<UserChallengeInfoDTO> userInfos = new ArrayList<>();

        for (ChallengeTemplateEntity challenge : challenges) {

            int challengeDays = challenge.getDays();
            List<String> days = convertToList(challengeDays);

            UserChallengeInfoDTO userInfo = UserChallengeInfoDTO.builder()
                    .challengePk(challenge.getPk())
                    .challengeTitle(challenge.getTitle())
                    .goal(challenge.getGoal())
                    .type(challenge.getType())
                    .alert(challenge.isAlert())
                    .hour(challenge.getHour())
                    .minute(challenge.getMinute())
                    .days(days)
                    .build();
            userInfos.add(userInfo);
        }

        return TodayChallengesDTO.Response.builder()
                .success(true)
                .userInfos(userInfos)
                .build();
    }

    @Transactional
    @Override
    public ChallengeAddDTO.Response addChallenge(Long userPk, ChallengeAddDTO.Request dto){

        // [1] 유효성 검사

        // [1-1] 필수 조건들이 누락되어 있을 때 (제목, 목표 횟수, 종류, 반복 요일)
        if (dto.getTitle() == null || dto.getGoal() == 0 || dto.getType() == null || dto.getDays().size() == 0) {
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }
        // [1-2] 알람은 설정했으나 시간, 분 정보가 들어오지 않았을 때
        if (dto.isAlert() && (dto.getHour() == null || dto.getMinute() == null)){
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }
        // [1-3] 알람을 설정하지 않았으나 시간, 분 정보가 들어올 때
        if (!dto.isAlert() && (dto.getHour() != null || dto.getMinute() != null)){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-4] 입력 가능한 시간인지 검사
        if(dto.getHour() != null && (dto.getHour() < 0 || dto.getHour() >= 24)){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-5] 입력 가능한 분인지 검사
        if (dto.getMinute() != null && ( dto.getMinute() < 0 || dto.getMinute() >= 60)){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // [2] 저장
        // [2-1] 요일 변환
        int days = convertToInteger(dto.getDays());

        // [2-2] db 저장
        ChallengeTemplateEntity challenge = ChallengeTemplateEntity.builder()
                .title(dto.getTitle())
                .goal(dto.getGoal())
                .type(dto.getType().name())
                .alert(dto.isAlert())
                .hour(dto.getHour())
                .minute(dto.getMinute())
                .days(days)
                .userPk(userPk)
                .build();

        challengeTemplateRepository.save(challenge);

        return ChallengeAddDTO.Response.builder()
                .success(true)
                .build();
    }

    @Transactional
    @Override
    public ChallengeDeleteDTO.Response deleteChallenge(Long userPk, ChallengeDeleteDTO.Request dto){

        challengeTemplateRepository.deleteById(dto.getChallengePk());

        return ChallengeDeleteDTO.Response.builder()
                .success(true)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserChallengeAllDTO.Response userChallengesAll(Long userPk){

        LocalDateTime start = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        LocalDateTime end = start.plusDays(1);

        List<ChallengeTemplateEntity> userChallenges = challengeTemplateRepository.findAllChallengesByUser(1l);

        List<UserChallengeAllDTO.Info> challenges = new ArrayList<>();

        for (ChallengeTemplateEntity challenge : userChallenges){
            long challengePk = challenge.getPk();
            int logs = challengeLogRepository.findChallengeLogs(start, end, challengePk);
            List<String> days = convertToList(challenge.getDays());

            UserChallengeAllDTO.Info info = UserChallengeAllDTO.Info.builder()
                    .challengePk(challenge.getPk())
                    .type(ChallengeTypeEnum.valueOf(challenge.getType()))
                    .title(challenge.getTitle())
                    .count(logs)
                    .goal(challenge.getGoal())
                    .alert(challenge.isAlert())
                    .hour(challenge.getHour())
                    .minute(challenge.getMinute())
                    .days(days)
                    .build();
            challenges.add(info);
        }
        return UserChallengeAllDTO.Response.builder()
                .success(true)
                .response(challenges)
                .build();


    }

    @Override
    @Transactional(readOnly = true)
    public ChallengePokeDTO.Response infoForPoke(Long userPk, ChallengePokeDTO.Request dto){
        ChallengePokeDTO.Info info = new ChallengePokeDTO.Info();

        try {
            ChallengeTemplateEntity challenge = challengeTemplateRepository.findChallengeByPk(dto.getChallengePk());
            info = ChallengePokeDTO.Info.builder()
                    .challengeTitle(challenge.getTitle())
                    .userPk(challenge.getUserPk())
                    .build();
        } catch(Exception e){
            log.info(e.getMessage());
        }

        return ChallengePokeDTO.Response.builder()
                .success(true)
                .response(info)
                .build();
    }

    // 단일 챌린지 조회

    private List<String> convertToList(int challengeDays){
        List<String> days = new ArrayList<>();

        if ((challengeDays&1) > 0){
            days.add("일");
        }
        if ((challengeDays&2) > 0){
            days.add("월");
        }
        if ((challengeDays&4) > 0){
            days.add("화");
        }
        if ((challengeDays&8) > 0){
            days.add("수");
        }
        if ((challengeDays&16) > 0){
            days.add("목");
        }
        if ((challengeDays&32) > 0){
            days.add("금");
        }
        if ((challengeDays&64) > 0){
            days.add("토");
        }

        return days;
    }

    private int convertToInteger(List<String> daysInfo){
        int result = 0;

        for (String day : daysInfo) {
            switch (day) {
                case "일":
                    result += 1;
                    break;
                case "월":
                    result += 2;
                    break;
                case "화":
                    result += 4;
                    break;
                case "수":
                    result += 8;
                    break;
                case "목":
                    result += 16;
                    break;
                case "금":
                    result += 32;
                    break;
                case "토":
                    result += 64;
                    break;
            }
        }
        return result;
    }
}