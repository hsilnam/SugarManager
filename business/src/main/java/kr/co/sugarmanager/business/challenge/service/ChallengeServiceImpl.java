package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.TodayChallengesDTO;
import kr.co.sugarmanager.business.challenge.dto.UserChallengeInfoDTO;
import kr.co.sugarmanager.business.challenge.entity.ChallengeTemplateEntity;
import kr.co.sugarmanager.business.challenge.repository.ChallengeLogRepository;
import kr.co.sugarmanager.business.challenge.repository.ChallengeTemplateRepository;
import kr.co.sugarmanager.business.challenge.repository.SettingsRepository;
import kr.co.sugarmanager.business.challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeLogRepository challengeLogRepository;

    // 현욱이가 만들어주면 삭제
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

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
            List<String> days = convert(challengeDays);

            UserChallengeInfoDTO userInfo = UserChallengeInfoDTO.builder()
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
                .userInfos(userInfos)
                .build();
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