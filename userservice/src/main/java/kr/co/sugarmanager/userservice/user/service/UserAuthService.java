package kr.co.sugarmanager.userservice.user.service;

import kr.co.sugarmanager.userservice.user.dto.JoinDTO;
import kr.co.sugarmanager.userservice.user.dto.LoginDTO;
import kr.co.sugarmanager.userservice.user.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.user.dto.SocialLoginDTO;

public interface UserAuthService {
    SocialLoginDTO.Response socialLogin(SocialLoginDTO.Request dto);

    SocialLoginDTO.Response socialLogin(String code);//임시로 client에서 소셜로그인 할 수 있도록

    RefreshDTO.Response refreshToken(RefreshDTO.Request dto);

    LoginDTO.Response login(LoginDTO.Request req);

    JoinDTO.Response join(JoinDTO.Request req);
}
