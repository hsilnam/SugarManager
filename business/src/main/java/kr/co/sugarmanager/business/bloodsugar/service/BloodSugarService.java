package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.*;

public interface BloodSugarService {
    BloodSugarSaveDTO.Response save(Long userPk, BloodSugarSaveDTO.Request request);
    BloodSugarUpdateDTO.Response update(Long userPk, BloodSugarUpdateDTO.Request request);
    BloodSugarDeleteDTO.Response delete(Long userPk, BloodSugarDeleteDTO.Request request);
    BloodSugarSelectDTO.Response select(Long userPk, String targetUserNickname, int year, int month, int day);
    BloodSugarPeriodDTO.Response selectPeriod(Long userPk, String targetUserNickname, String startDate, String endDate, int page);
}
