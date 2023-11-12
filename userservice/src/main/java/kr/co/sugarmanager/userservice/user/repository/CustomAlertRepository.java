package kr.co.sugarmanager.userservice.user.repository;

import kr.co.sugarmanager.userservice.user.vo.AlertType;

public interface CustomAlertRepository {
    long setAlarm(long pk, AlertType alertType, boolean status);
}
