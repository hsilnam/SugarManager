package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.*;
import kr.co.sugarmanager.business.challenge.entity.ChallengeLogEntity;
import kr.co.sugarmanager.business.challenge.entity.ChallengeTemplateEntity;
import kr.co.sugarmanager.business.challenge.repository.ChallengeLogRepository;
import kr.co.sugarmanager.business.challenge.repository.ChallengeTemplateRepository;
import kr.co.sugarmanager.business.challenge.repository.SettingsRepository;
import kr.co.sugarmanager.business.challenge.repository.UserRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    // 오늘의 챌린지 모두 가져오기
    @Transactional(readOnly = true)
    @Override
    public TodayChallengesDTO.Response todaysChallenges() {

        int day = 1 << (LocalDateTime.now().getDayOfWeek().getValue() - 1);
        List<ChallengeTemplateEntity> challenges = challengeTemplateRepository.findTodaysChallenges(day);

        List<ChallengeInfoDTO> userInfos = new ArrayList<>();

        for (ChallengeTemplateEntity challenge : challenges) {
            int challengeDays = challenge.getDays();
            List<String> days = convertToList(challengeDays);
            ChallengeInfoDTO userInfo = ChallengeInfoDTO.builder()
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
    public ChallengeAddDTO.Response addChallenge(Long pk, ChallengeAddDTO.Request dto) {
        // [1] 유효성 검사
        // [1-1] 인증되지 않은 유저
        if (!userRepository.isAuthorized(pk)) {
            throw new ValidationException(ErrorCode.UNAUTHORIZED_USER_ACCESS);
        }
        // [1-2] 그룹 멤버 아님
        if (!userRepository.inSameGroup(pk, dto.getNickname())) {
            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        // [1-3] 등록할 사람이 없는 유저
        if (userRepository.findIdByNickname(dto.getNickname()) == null) {
            throw new ValidationException(ErrorCode.NO_SUCH_USER);
        }
        // [1-4] 필수 조건들이 누락되어 있을 때 (제목, 목표 횟수, 종류, 반복 요일)
        if (dto.getTitle() == null || dto.getGoal() == 0 || dto.getType() == null || dto.getDays().isEmpty()) {
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }
        // [1-5] 알람은 설정했으나 시간, 분 정보가 들어오지 않았을 때
        if (dto.isAlert() && (dto.getHour() == null || dto.getMinute() == null)) {
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }
        // [1-6] 알람을 설정하지 않았으나 시간, 분 정보가 들어올 때
        if (!dto.isAlert() && (dto.getHour() != null || dto.getMinute() != null)) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-7] 입력 가능한 시간인지 검사
        if (dto.getHour() != null && (dto.getHour() < 0 || dto.getHour() >= 24)) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-8] 입력 가능한 분인지 검사
        if (dto.getMinute() != null && (dto.getMinute() < 0 || dto.getMinute() >= 60)) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-9] 타입오류
        if (ChallengeTypeEnum.valueOf(String.valueOf(dto.getType())) == null) {
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
                .userPk(userRepository.findIdByNickname(dto.getNickname()))
                .build();
        challengeTemplateRepository.save(challenge);
        return ChallengeAddDTO.Response.builder()
                .success(true)
                .build();
    }

    @Transactional
    @Override
    public ChallengeDeleteDTO.Response deleteChallenge(Long pk, ChallengeDeleteDTO.Request dto) {

        // [1] 유효성 검사
        // [1-1] 인증되지 않은 유저
        if (!userRepository.isAuthorized(pk)) {
            throw new ValidationException(ErrorCode.UNAUTHORIZED_USER_ACCESS);
        }
        // [2] 삭제
        List<ChallengeDeleteDTO.DeleteInfo> deleteList = dto.getDeleteList();
        for (ChallengeDeleteDTO.DeleteInfo challenge : deleteList) {
            // [1-2] 같은 그룹이 아닌 유저의 챌린지 삭제 시 403 에러
            if (!userRepository.inSameGroup(pk, challenge.getNickname())) {
                throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
            } else {
                challengeTemplateRepository.deleteById(challenge.getChallengePk());
            }
        }
        return ChallengeDeleteDTO.Response.builder()
                .success(true)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserChallengeAllDTO.Response userChallengesAll(Long pk, String nickname) {

        // [1] 유효성 검사
        // [1-1] 권한 관련
        if (!userRepository.isAuthorized(pk)) {
            throw new ValidationException(ErrorCode.UNAUTHORIZED_USER_ACCESS);
        }
        // [1-2] 없는 유저일 때
        if (userRepository.findIdByNickname(nickname) == null) {
            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        // [1-3] 내 그룹이 아니거나 내 것이 아닐 때
        if (userRepository.findGroupIdByNickname(nickname) != null){
            if (!userRepository.inSameGroup(pk, nickname)){
                throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
        }
        else {
            if (!Objects.equals(userRepository.findIdByNickname(nickname), pk)){
                throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
        }

        Long userPk = userRepository.findIdByNickname(nickname);

        // [2] 조회
        LocalDateTime start = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        LocalDateTime end = start.plusDays(1);

        List<ChallengeTemplateEntity> userChallenges = challengeTemplateRepository.findAllChallengesByUser(userPk);

        List<UserChallengeAllDTO.Info> list = new ArrayList<>();

        for (ChallengeTemplateEntity challenge : userChallenges) {
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
            list.add(info);
        }

        UserChallengeAllDTO.InfoResponse infoResponse = UserChallengeAllDTO.InfoResponse.builder()
                .pokeAbled(settingsRepository.isPokeAlarm(userRepository.findIdByNickname(nickname)))
                .list(list)
                .build();

        return UserChallengeAllDTO.Response.builder()
                .success(true)
                .response(infoResponse)
                .build();


    }

    // 단일 챌린지 조회
    public UserChallengeInfoDTO.Response userChallengeInfo(Long pk, String nickname, Long challengePk) {

        // [1] 유효성 검사
        // [1-1] 권한 관련
        if (userRepository.isAuthorized(pk)) {
            throw new ValidationException(ErrorCode.UNAUTHORIZED_USER_ACCESS);
        }
        // [1-2] 없는 유저일 때
        if (userRepository.findIdByNickname(nickname) == null) {
            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        // [1-3] 내 그룹이 아니거나 내 것이 아닐 때
        if (userRepository.findGroupIdByNickname(nickname) != null){
            if (!userRepository.inSameGroup(pk, nickname)){
                throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
        }
        else {
            if (!Objects.equals(userRepository.findIdByNickname(nickname), pk)){
                throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
            }
        }
        LocalDateTime start = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        LocalDateTime end = start.plusDays(1);

        UserChallengeAllDTO.Info info = new UserChallengeAllDTO.Info();

        try {
            ChallengeTemplateEntity challenge = challengeTemplateRepository.findChallengeByPk(challengePk);

            Integer logs = challengeLogRepository.findChallengeLogs(start, end, challengePk);
            List<String> days = convertToList(challenge.getDays());
            info = UserChallengeAllDTO.Info.builder()
                    .challengePk(challengePk)
                    .type(ChallengeTypeEnum.valueOf(challenge.getType()))
                    .title(challenge.getTitle())
                    .count(logs)
                    .goal(challenge.getGoal())
                    .alert(challenge.isAlert())
                    .hour(challenge.getHour())
                    .minute(challenge.getMinute())
                    .days(days)
                    .build();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return UserChallengeInfoDTO.Response.builder()
                .success(true)
                .response(info)
                .build();
    }

    @Override
    @Transactional
    public ChallengeClaimDTO.Response claim(Long pk, Long challengePk) {
        if (challengeTemplateRepository.findChallengeByPk(challengePk).getUserPk() != pk) {
            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        ChallengeLogEntity log = ChallengeLogEntity.builder()
                .challengeTemplatePk(challengePk)
                .build();
        challengeLogRepository.save(log);
        return ChallengeClaimDTO.Response.builder()
                .success(true)
                .build();
    }

    private List<String> convertToList(int challengeDays) {
        List<String> days = new ArrayList<>();

        if ((challengeDays & 1) > 0) {
            days.add("일");
        }
        if ((challengeDays & 2) > 0) {
            days.add("월");
        }
        if ((challengeDays & 4) > 0) {
            days.add("화");
        }
        if ((challengeDays & 8) > 0) {
            days.add("수");
        }
        if ((challengeDays & 16) > 0) {
            days.add("목");
        }
        if ((challengeDays & 32) > 0) {
            days.add("금");
        }
        if ((challengeDays & 64) > 0) {
            days.add("토");
        }

        return days;
    }

    private int convertToInteger(List<String> daysInfo) {
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