package kr.co.sugarmanager.userservice.repository;

import kr.co.sugarmanager.userservice.entity.AlertType;

public interface CustomAlertRepository {
    long setAlarm(long pk, AlertType alertType, boolean status);
}
