package kr.co.sugarmanager.business.timeline.service;

import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.challenge.repository.UserRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
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

    @Override
    @Transactional(readOnly = true)
    public TimelineMonthDTO.Response timelineMonth(String nickname, Integer year, Integer month){
//Long id, String nickname, Integer year, Integer month){
        // [1] 유효성 검사
        // [1-1] 접근 권한이 있는 유저인지 검사
//        if (!Objects.equals(userRepository.findNicknameById(id), nickname) || !userRepository.inSameGroup(id, nickname)){
//            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
//        }
        // [1-2] 요청하는 날짜 포맷을 벗어난 경우
        if(0 >= month || month > 12 || 1970 > year || year > 2099 ){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-3] 해당 닉네임이 없을 때
        if(userRepository.findIdByNickname(nickname) == null){
            throw new ValidationException(ErrorCode.NO_SUCH_USER);
        }

        Long searchUserPk = userRepository.findIdByNickname(nickname);
        log.info("nickname : {} year : {} month : {}", nickname, year, month);

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
    public TimelineDateDTO.Response timelineDate(String nickname, Integer year, Integer month, Integer date) {
        //Long id, String nickname, Integer year, Integer month, Integer date){

        // [1] 유효성 검사
        // [1-1] 접근 권한이 있는 유저인지 검사
//        if (!Objects.equals(userRepository.findNicknameById(id), nickname) || !userRepository.inSameGroup(id, nickname)){
//            throw new ValidationException(ErrorCode.HANDLE_ACCESS_DENIED);
//        }
        // [1-2] 요청하는 날짜 포맷을 벗어난 경우
        if(0 >= month || month > 12 || 1970 > year || year > 2099 || date <= 0 || date > 31){
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // [1-3] 해당 닉네임이 없을 때
        if(userRepository.findIdByNickname(nickname) == null){
            throw new ValidationException(ErrorCode.NO_SUCH_USER);
        }

        List<TimelineDateDTO.Info> infos = new ArrayList<>();

        Long searchUserPk = userRepository.findIdByNickname(nickname);
        LocalDateTime start = LocalDateTime.of(year, month, date, 0, 0, 0);
        LocalDateTime end = start.plusDays(1);

        List<MenuEntity> menu = menuRepository.findMenuRecordsForDay(searchUserPk,start,end);
        for(MenuEntity m : menu){
            infos.add(TimelineDateDTO.Info.builder()
                            .hour(m.getCreatedAt().getHour())
                            .minute(m.getCreatedAt().getMinute())
                            .second(m.getCreatedAt().getSecond())
                            .category(InfoTypeEnum.valueOf("MENU"))
//                            .content(m.get)
                            .build());
        }



        return TimelineDateDTO.Response.builder()
                .success(true)
                .response(null)
                .build();
    }

}
