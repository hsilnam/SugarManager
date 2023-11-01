package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.service;

import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto.AlarmBloodsugarDTO;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto.UserInfo;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity.MenuEntity;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity.UserSettingEntity;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository.MenuRepository;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository.SettingRepository;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository.UserRepository;
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
public class AlarmBloodsugarServiceImpl implements  AlarmBloodsugarService{

    private final MenuRepository menuRepository;
    private final BloodSugarRepository bloodSugarRepository;

    // 현욱이가 만들어주면 삭제
    private final SettingRepository settingRepository;
    private final UserRepository userRepository;

    @Override
    public AlarmBloodsugarDTO.Response bloodSugarAlarms() {

        List<UserInfo> users = new ArrayList<>();

        // [1] settings 에서 혈당 체크 알람을 켜 둔 유저 리스트 찾기
        List<UserSettingEntity> userList = settingRepository.findAlertUserList() ;

        // [2] 알람 전송 대상자 여부 확인
        for (UserSettingEntity userSetting : userList) {
            // [2-1] menu 테이블의 가장 최신 created_at 확인
            MenuEntity latestMenu = menuRepository.findLatestMenu(userSetting.getUserPk());
            log.info("latestMenu : {}", latestMenu);

            // [2-2] 혈당 기록 여부 검사
            LocalDateTime start = latestMenu.getCreated_at();
            // 현욱이가 만들어주면 수정
            LocalDateTime end = start.plusHours(userSetting.getSugarAlertHour());
            BloodSugarEntity record = bloodSugarRepository.checkRecord(start, end);
            log.info("check data: {} ", record);

            // [2-3] 대상자일 경우 추가
            if (record == null) {
                UserInfo user = UserInfo.builder()
                        .type("BLOODSUGAR")
                        // 현욱이가 만들어주면 수정
                        .nickname(userRepository.findNicknameById(userSetting.getUserPk()))
                        .fcmToken(userSetting.getFcmToken())
                        .build();
                users.add(user);
            }
        }

        return AlarmBloodsugarDTO.Response.builder()
                .userInfos(users)
                .build();
    }


}
