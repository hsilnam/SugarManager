package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.UserInfoDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoUpdateDTO;

public interface UserService {
    UserInfoDTO.Response getMemberInfo(UserInfoDTO.Request req);

    UserInfoUpdateDTO.Response updateMemberInfo(UserInfoUpdateDTO.Request req);
}
