package kr.co.sugarmanager.alarm.service;

public interface AlarmReceiveService {
    void consumeChallengeAlarm(String message);
    void consumeBloodSugarAlarm(String message);
    void consumePokeAlarm(String message);

}
