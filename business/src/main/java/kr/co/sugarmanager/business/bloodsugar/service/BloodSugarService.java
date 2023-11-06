package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;

public interface BloodSugarService {
    BloodSugarSaveDTO.Response save(Long userPk, BloodSugarSaveDTO.Request request);
}
