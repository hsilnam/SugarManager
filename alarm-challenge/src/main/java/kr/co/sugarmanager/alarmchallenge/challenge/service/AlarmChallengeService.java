package kr.co.sugarmanager.alarmchallenge.challenge.service;

import kr.co.sugarmanager.alarmchallenge.challenge.dto.AlarmChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.RemindChallengeDTO;

public interface AlarmChallengeService {
    AlarmChallengeDTO.Response getChallanges();
    RemindChallengeDTO.Response remind();
}
