package kr.co.sugarmanager.alarmchallenge.challenge.service;

import kr.co.sugarmanager.alarmchallenge.challenge.dto.*;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeTemplateEntity;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.UserSettingEntity;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.ChallengeLogRepository;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.ChallengeTemplateRepository;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.SettingsRepository;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.server.UID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AlarmChallengeServiceImpl implements AlarmChallengeService{
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeLogRepository challengeLogRepository;

    // 현욱이가 만들어주면 삭제
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    // 10분마다 챌린지 알람 가져오기
    @Transactional(readOnly = true)
    @Override
    public AlarmChallengeDTO.Response getChallanges(){

        LocalDateTime time = LocalDateTime.now().plusMinutes(1);
        int hour = time.getHour();
        int minute = time.getMinute();
        int day = 1 << (time.getDayOfWeek().getValue()-1);

        log.info("=====================");
        log.info("{}시 {}분 챌린지 알람 가져온다아아아아", hour, minute);
        log.info("=====================");

        List<ChallengeTemplateEntity> challenges = challengeTemplateRepository.findChallenges(day, hour, minute);
        log.info("challenges : {}", challenges);
        List<UserInfoDTO> userInfos = new ArrayList<>();
        if (challenges.size() > 0) {

            log.info("challenge : {}", challenges.get(0).getCreatedAt());
            log.info("challenges : {}", challenges);

            for (ChallengeTemplateEntity challenge : challenges){

                Long userPk = challenge.getUserPk();
                log.info("userPk : {}", userPk);

                // 현욱이가 만들어주면 수정
                try {
                    UserSettingEntity setting = settingsRepository.findSettingByUserId(userPk);
                    if (challenge.isAlert() && challenge.getDeletedAt() == null){
                        UserInfoDTO userInfo = UserInfoDTO.builder()
                                .nickname(userRepository.findNicknameById(setting.getUserPk()))
                                .fcmToken(setting.getFcmToken())
                                .challengeTitle(challenge.getTitle())
                                .hour(challenge.getHour())
                                .minute(challenge.getMinute())
                                .build();
                        userInfos.add(userInfo);
                    }
                } catch (Exception e){
                    log.info(e.getMessage());
                }
            }
            log.info("userInfos size : {}", userInfos.size());
        }
        return AlarmChallengeDTO.Response.builder()
                .userInfos(userInfos)
                .build();
    }

    @Override
    public RemindChallengeDTO.Response remind(){

        // [1] 가져올 유저 조건
        List<RemindUserInfoDTO> userInfos = new ArrayList<>();

      List<UserSettingEntity> users = settingsRepository.findUsersWithChallengeAlarmOn();
        for (UserSettingEntity user : users){
            log.info("user nickname : {}", userRepository.findNicknameById(user.getPk()));
            RemindUserInfoDTO info = RemindUserInfoDTO.builder()
                    .nickname(userRepository.findNicknameById(user.getUserPk()))
                    .fcmToken(user.getFcmToken())
                    .build();
            userInfos.add(info);
        }

        return RemindChallengeDTO.Response.builder()
                .userInfos(userInfos)
                .build();

        // [1-1] 오늘 챌린지가 있는 모든 유저 가져오기
//        int day = 1 << (LocalDateTime.now().getDayOfWeek().getValue()-1);
//        List<ChallengeTemplateEntity> challenges = challengeTemplateRepository.findTodaysChallenges(day);
//        log.info("size : {} , challenges : {}", challenges.size(), challenges);
//
//        for(ChallengeTemplateEntity challenge : challenges){
//
//            long userPk = challenge.getUserPk();
//
//            RemindUserInfoDTO userInfo = RemindUserInfoDTO.builder()
//                    .nickname(userRepository.findNicknameById(userPk))
//                    .fcmToken(settingsRepository.findSettingByUserId(userPk).getFcmToken())
//                    .build();
//
//            // [1-2] 오늘의 챌린지 완료 횟수 count
//            long challengePk = challenge.getPk();
//
//            LocalDateTime start = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();
//            LocalDateTime end = start.plusDays(1);
//            int cnt = challengeLogRepository.findAllChallenges(start, end, challengePk);
//
//            if (cnt < challenge.getGoal() && !userInfos.contains(userInfo) ) {
//                userInfos.add(userInfo);
//            }
//        }
//
//        return RemindChallengeDTO.Response.builder()
//                .userInfos(userInfos)
//                .build();
    }

    private List<String> convert(int challengeDays){
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

}
