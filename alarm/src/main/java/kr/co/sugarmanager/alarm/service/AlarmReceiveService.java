package kr.co.sugarmanager.alarm.service;

public interface AlarmReceiveService {
    void consumeAlarm(String kafkaMessage);

}
