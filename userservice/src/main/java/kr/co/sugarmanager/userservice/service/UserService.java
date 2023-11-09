package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.AlarmDTO;
import kr.co.sugarmanager.userservice.dto.AlarmUpdateDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoUpdateDTO;

public interface UserService {
    UserInfoDTO.Response getMemberInfo(UserInfoDTO.Request req);

    UserInfoUpdateDTO.Response updateMemberInfo(UserInfoUpdateDTO.Request req);

    AlarmDTO.Response getAlarm(AlarmDTO.Request req);

    AlarmUpdateDTO.Response setAlarm(AlarmUpdateDTO.Request req);
}
