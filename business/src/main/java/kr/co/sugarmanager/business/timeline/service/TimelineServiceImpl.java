package kr.co.sugarmanager.business.timeline.service;

import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.challenge.dto.LogsAndLatestInterface;
import kr.co.sugarmanager.business.challenge.entity.ChallengeTemplateEntity;
import kr.co.sugarmanager.business.challenge.repository.ChallengeLogRepository;
import kr.co.sugarmanager.business.challenge.repository.ChallengeTemplateRepository;
import kr.co.sugarmanager.business.global.user.repository.UserRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import kr.co.sugarmanager.business.timeline.dto.InfoTypeEnum;
import kr.co.sugarmanager.business.timeline.dto.TimelineDateDTO;
import kr.co.sugarmanager.business.timeline.dto.TimelineMonthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService{

    private final UserRepository userRepository;
    private final BloodSugarRepository bloodSugarRepository;
    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final ChallengeTemplateRepository challengeTemplateRepository;
    private final ChallengeLogRepository challengeLogRepository;

    @Override
    @Transactional(readOnly = true)
    public TimelineMonthDTO.Response timelineMonth(Long pk, String nickname, Integer year, Integer month){
        String loggedInUserNickname = userRepository.findNicknameById(pk)
                .orElseThrow(() -> new ValidationException(ErrorCode.NO_SUCH_USER));
        // [1] 유효성 검사
        // [1-1] 접근 권한이 있는 유저인지 검사
        if (!Objects.equals(loggedInUserNickname, nickname) && !userRepository.inSameGroup(pk, nickname)){
            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        // [1-2] 요청하는 날짜 포맷을 벗어난 경우
        if(0 >= month || month > 12 || 1970 > year || year > 2099 ){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-3] 해당 닉네임이 없을 때
        Long searchUserPk = userRepository.findIdByNickname(nickname)
                .orElseThrow(()->new ValidationException(ErrorCode.NO_SUCH_USER));
        // [1-4] 인증된 유저 검사
        if(!userRepository.isAuthorized(pk)){
            throw new ValidationException(ErrorCode.UNAUTHORIZED_USER_ACCESS);
        }
        // [2] 해당 월의 기록들 불러오기
        List<LocalDate> total = new ArrayList<>();
        List<LocalDate> bloodsugar = bloodSugarRepository.findBloodSugarRecordsForMonth(searchUserPk,year, month);
        List<LocalDate> menu = menuRepository.findMenuRecordsForMonth(searchUserPk,year,month);
        for (LocalDate record : bloodsugar){
            if (!total.contains(record)){
                total.add(record);
            }
        }
        for (LocalDate record : menu){
            if (!total.contains(record)){
                total.add(record);
            }
        }
        Collections.sort(total);
        log.info(Arrays.toString(new List[]{total}));

        return TimelineMonthDTO.Response.builder()
                .success(true)
                .response(total)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TimelineDateDTO.Response timelineDate(Long pk, String nickname, Integer year, Integer month, Integer date) {
        String loggedInUserNickname = userRepository.findNicknameById(pk)
                .orElseThrow(() -> new ValidationException(ErrorCode.NO_SUCH_USER));
        // [1] 유효성 검사
        // [1-1] 접근 권한이 있는 유저인지 검사
        if (!Objects.equals(loggedInUserNickname, nickname) && !userRepository.inSameGroup(pk, nickname)){
            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        // [1-2] 요청하는 날짜 포맷을 벗어난 경우
        if(0 >= month || month > 12 || 1970 > year || year > 2099 || date <= 0 || date > 31){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-3] 해당 닉네임이 없을 때
        Long searchUserPk = userRepository.findIdByNickname(nickname)
                .orElseThrow(()->new ValidationException(ErrorCode.NO_SUCH_USER));
        // [1-4] 인증된 유저 검사
        if(!userRepository.isAuthorized(pk)){
            throw new ValidationException(ErrorCode.UNAUTHORIZED_USER_ACCESS);
        }

        List<TimelineDateDTO.Info> infos = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(year, month, date, 0, 0, 0);
        LocalDateTime end = start.plusDays(1);

        List<MenuEntity> menu = menuRepository.findMenuRecordsForDay(searchUserPk,start,end);
        for(MenuEntity m : menu){
            infos.add(TimelineDateDTO.Info.builder()
                            .hour(m.getRegistedAt().getHour())
                            .minute(m.getRegistedAt().getMinute())
                            .second(m.getRegistedAt().getSecond())
                            .category(InfoTypeEnum.MENU)
                            .content(String.valueOf(foodRepository.caloriesPerMenu(m.getMenuPk())))
                            .build());
        }

        List<BloodSugarEntity> bloodSugar = bloodSugarRepository.findBloodSugarRecordsForDay(searchUserPk,start,end);
        for (BloodSugarEntity b : bloodSugar){
            infos.add(TimelineDateDTO.Info.builder()
                            .hour(b.getRegistedAt().getHour())
                            .minute(b.getRegistedAt().getMinute())
                            .second(b.getRegistedAt().getSecond())
                            .category(InfoTypeEnum.BLOODSUGAR)
                            .content(String.valueOf(b.getLevel()))
                            .build());
        }

        List<ChallengeTemplateEntity> challenge = challengeTemplateRepository.findAllChallengesByUser(searchUserPk)
                .orElse(null);
        if (challenge != null) {
            for (ChallengeTemplateEntity c : challenge) {
                try {
                    LogsAndLatestInterface logs = challengeLogRepository.findChallengeLogsAndLatestUpdatedTime(start, end, c.getPk());
                    if (logs.getCount() >= c.getGoal()) {
                        infos.add(TimelineDateDTO.Info.builder()
                                .hour(logs.getLatest().getHour())
                                .minute(logs.getLatest().getMinute())
                                .second(logs.getLatest().getSecond())
                                .category(InfoTypeEnum.CHALLENGE)
                                .content(c.getTitle())
                                .complete(true)
                                .build());
                    }
                } catch (Exception ignored) {
                }
            }
        }

        Collections.sort(infos);

        return TimelineDateDTO.Response.builder()
                .success(true)
                .response(infos)
                .build();
    }

}
