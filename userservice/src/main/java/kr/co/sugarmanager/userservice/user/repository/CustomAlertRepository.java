package kr.co.sugarmanager.userservice.user.repository;

import kr.co.sugarmanager.userservice.user.dto.AlarmUpdateDTO;

public interface CustomAlertRepository {
    long setAlarm(long pk, AlarmUpdateDTO.Request req);
}
