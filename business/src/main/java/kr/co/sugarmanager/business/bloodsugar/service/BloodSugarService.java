package kr.co.sugarmanager.business.bloodsugar.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarDeleteDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarUpdateDTO;

public interface BloodSugarService {
    BloodSugarSaveDTO.Response save(Long userPk, BloodSugarSaveDTO.Request request);
    BloodSugarUpdateDTO.Response update(Long userPk, BloodSugarUpdateDTO.Request request);
    BloodSugarDeleteDTO.Response delete(Long userPk, BloodSugarDeleteDTO.Request request);
}
