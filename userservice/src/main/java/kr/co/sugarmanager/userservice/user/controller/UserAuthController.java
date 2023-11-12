package kr.co.sugarmanager.userservice.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.sugarmanager.userservice.user.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.user.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.user.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static kr.co.sugarmanager.userservice.global.util.APIUtils.*;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {
    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirect;

    private final UserAuthService userAuthService;

    @PostMapping("/{socialType}")
    @ResponseBody
    public ResponseEntity<ApiResult<SocialLoginDTO.Response>> socialLogin(
            @PathVariable("socialType") String socialType,
            @RequestBody SocialLoginDTO.Request body
    ) {
        return result(true, userAuthService.socialLogin(body), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    @ResponseBody
    public ResponseEntity<ApiResult<RefreshDTO.Response>> refreshToken(
            @RequestBody RefreshDTO.Request dto
    ) {
        return result(true,userAuthService.refreshToken(dto),HttpStatus.OK);
    }

    //아래는 클라이언트에서 테스트용으로 만든 컨트롤러
    @GetMapping("/socialLogin")
    public String redirectSocialLogin() {
        return "redirect:https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirect + "&response_type=code";
    }

    @GetMapping("/login/oauth2/code/kakao")
    @ResponseBody
    public ResponseEntity<ApiResult<SocialLoginDTO.Response>> socialLoginWithJS(@RequestParam("code") String code) throws JsonProcessingException {
        return result(true, userAuthService.socialLogin(code), HttpStatus.OK);
    }
}
