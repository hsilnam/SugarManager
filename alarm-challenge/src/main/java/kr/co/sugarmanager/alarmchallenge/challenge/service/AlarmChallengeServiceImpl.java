package kr.co.sugarmanager.alarmchallenge.challenge.service;

import kr.co.sugarmanager.alarmchallenge.challenge.dto.AlarmChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.UserInfo;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeLogEntity;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeTemplateEntity;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.UserSettingEntity;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.ChallengeLogRepository;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.ChallengeTemplateRepository;
import kr.co.sugarmanager.alarmchallenge.challenge.repository.SettingsRepository;
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
    private final SettingsRepository settingsRepository;

    @Transactional(readOnly = true)
    @Override
    public AlarmChallengeDTO.Response todaysChallenges(){

        LocalDateTime start = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        LocalDateTime end = start.plusDays(1);
        log.info("start : {} end : {} ", start ,end);

        List<ChallengeLogEntity> challenges = challengeLogRepository.findAllChallenges(start, end);

        log.info("challenges : {}", challenges);


        List<UserInfo> userInfos = new ArrayList<>();
        for (ChallengeLogEntity challenge : challenges){
            ChallengeTemplateEntity challengeTemplate = challengeTemplateRepository.findByChallengeId(challenge.getChallengeTemplatePk());
            Long userPk = challengeTemplate.getUserPk();
            log.info("userPk : {}", userPk);

            // 현욱이가 만들어주면 수정
            UserSettingEntity setting = settingsRepository.findSettingByUserId(userPk);

            UserInfo userInfo = UserInfo.builder()
                    .fcmToken(setting.getFcmToken())
                    .challengeAlert(setting.isChallengeAlert())
                    .hour(challengeTemplate.getHour())
                    .minute(challengeTemplate.getMinute())
                    .deleted_at(challengeTemplate.getDeleted_at())
                    .build();
            if (userInfo.isChallengeAlert() && userInfo.getDeleted_at() == null){
                userInfos.add(userInfo);
            }
            log.info("userPk : {}, userInfo : {} ", userPk, userInfo);
        }

        log.info("userInfos size : {}", userInfos.size());

        return AlarmChallengeDTO.Response.builder()
                .userInfos(userInfos)
                .build();
    }

}
