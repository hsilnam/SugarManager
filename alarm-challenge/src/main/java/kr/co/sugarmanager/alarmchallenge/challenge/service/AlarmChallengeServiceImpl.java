package kr.co.sugarmanager.alarmchallenge.challenge.service;

import kr.co.sugarmanager.alarmchallenge.challenge.dto.AlarmChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.UserInfoDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeLogEntity;
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

    @Transactional(readOnly = true)
    @Override
    public AlarmChallengeDTO.Response getChallanges(){

        log.info("=====================");
        log.info("시작한다아아아");
        log.info("=====================");

        // 가져올 알람 조건
        LocalDateTime start = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        LocalDateTime end = start.plusDays(1);
        LocalDateTime time = LocalDateTime.now().plusMinutes(1);
        int hour = time.getHour();
        int minute = time.getMinute();
        log.info("hour : {} minute : {}", hour, minute);

        List<ChallengeLogEntity> challenges = challengeLogRepository.findAllChallenges(start,end, hour, minute);

        List<UserInfoDTO> userInfos = new ArrayList<>();
        if (challenges.size() > 0) {

            log.info("challenge : {}", challenges.get(0).getCreatedAt());
            log.info("challenges : {}", challenges);

            for (ChallengeLogEntity challenge : challenges){
                log.info("challenge pk : {}, challenge template pk : {}", challenge.getPk(), challenge.getChallengeTemplatePk());
                ChallengeTemplateEntity challengeTemplate = challengeTemplateRepository.findByChallengeId(challenge.getChallengeTemplatePk());

                Long userPk = challengeTemplate.getUserPk();
                log.info("userPk : {}", userPk);

                // 현욱이가 만들어주면 수정
                try {
                    UserSettingEntity setting = settingsRepository.findSettingByUserId(userPk);
                    if (challengeTemplate.isAlert() && challengeTemplate.getDeletedAt() == null){
                        UserInfoDTO userInfo = UserInfoDTO.builder()
                                .nickname(userRepository.findNicknameById(setting.getUserPk()))
                                .fcmToken(setting.getFcmToken())
                                .challengeTitle(challengeTemplate.getTitle())
                                .hour(challengeTemplate.getHour())
                                .minute(challengeTemplate.getMinute())
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

    private final SettingsRepository settingsRepository;

}
