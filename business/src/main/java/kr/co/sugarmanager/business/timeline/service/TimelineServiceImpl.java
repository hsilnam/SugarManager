package kr.co.sugarmanager.business.timeline.service;

import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.challenge.repository.UserRepository;
import kr.co.sugarmanager.business.global.exception.CustomException;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import kr.co.sugarmanager.business.timeline.dto.TimelineMonthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineServiceImpl implements TimelineService{

    private final UserRepository userRepository;
    private final BloodSugarRepository bloodSugarRepository;
    private final MenuRepository menuRepository;

    @Override
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
//        if(userRepository.findIdByNickname(nickname) == null){
//            throw new ValidationException(ErrorCode.NO_SUCH_USER);
//        }
        // [1-4] 토큰 유효성 검사 (리팩토링 때 ㄱㄱ)


        Long searchUserPk = userRepository.findIdByNickname(nickname);
        log.info("nickname : {} year : {} month : {}", nickname, year, month);
        // [2] 해당 월의 기록들 불러오기
        List<LocalDateTime> total = new ArrayList<>();
        List<LocalDateTime> bloodsugar = bloodSugarRepository.findBloodSugarRecordsForMonth(searchUserPk,year, month);
        List<LocalDateTime> menu = menuRepository.findMenuRecordsForMonth(searchUserPk,year,month);
        for (LocalDateTime record : bloodsugar){
            if (!total.contains(record)){
                total.add(record);
            }
        }
        for (LocalDateTime record : menu){
            if (!total.contains(record)){
                total.add(record);
            }
        }
        log.info(Arrays.toString(new List[]{total}));


        return null;
    }
}
