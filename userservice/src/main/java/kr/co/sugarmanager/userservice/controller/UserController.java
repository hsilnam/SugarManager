package kr.co.sugarmanager.userservice.controller;

import kr.co.sugarmanager.userservice.dto.UserInfoDTO;

import static kr.co.sugarmanager.userservice.util.APIUtils.*;

import kr.co.sugarmanager.userservice.service.UserService;
import kr.co.sugarmanager.userservice.util.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{nickname}")
    public ResponseEntity<ApiResult<UserInfoDTO.Response>> getMemberInfo(
            @PathVariable String nickname,
            @AuthenticationPrincipal JwtAuthentication auth
    ) {
        UserInfoDTO.Request req = UserInfoDTO.Request.builder()
                .userPk(auth != null ? auth.getPk() : 0l)
                .targetNickname(nickname)
                .build();
        return result(true, userService.getMemberInfo(req), HttpStatus.OK);
    }
}
