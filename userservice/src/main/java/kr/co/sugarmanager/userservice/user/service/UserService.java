package kr.co.sugarmanager.userservice.user.service;

import kr.co.sugarmanager.userservice.user.dto.*;

public interface UserService {
    UserInfoDTO.Response getMemberInfo(UserInfoDTO.Request req);

    UserInfoUpdateDTO.Response updateMemberInfo(UserInfoUpdateDTO.Request req);

    AlarmDTO.Response getAlarm(AlarmDTO.Request req);

    AlarmUpdateDTO.Response setAlarm(AlarmUpdateDTO.Request req);

    PokeDTO.Response poke(PokeDTO.Request req);
}
