package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.dto.SocialLoginDTO;

public interface UserAuthService {
    SocialLoginDTO.Response socialLogin(SocialLoginDTO.Request dto);

    SocialLoginDTO.Response socialLogin(String code);//임시로 client에서 소셜로그인 할 수 있도록

    RefreshDTO.Response refreshToken(RefreshDTO.Request dto);
}
