package kr.co.sugarmanager.alarmchallenge.challenge.service;

import kr.co.sugarmanager.alarmchallenge.challenge.dto.AlarmChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.RemindChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.TodayChallengesDTO;

public interface AlarmChallengeService {
    TodayChallengesDTO.Response todaysChallenges();
    AlarmChallengeDTO.Response getChallanges();
    RemindChallengeDTO.Response remind();
}
