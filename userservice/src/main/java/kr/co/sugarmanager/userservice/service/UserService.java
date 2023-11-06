package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.UserInfoDTO;

public interface UserService {
    UserInfoDTO.Response getMemberInfo(UserInfoDTO.Request req);
}
