package kr.co.sugarmanager.userservice.controller;

import kr.co.sugarmanager.userservice.dto.AlarmDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoDTO;

import static kr.co.sugarmanager.userservice.util.APIUtils.*;

import kr.co.sugarmanager.userservice.dto.UserInfoUpdateDTO;
import kr.co.sugarmanager.userservice.service.UserService;
import kr.co.sugarmanager.userservice.util.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/{nickname}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<UserInfoDTO.Response>> getMemberInfo(
            @PathVariable String nickname,
            @AuthenticationPrincipal JwtAuthentication auth
    ) {
        UserInfoDTO.Request req = UserInfoDTO.Request.builder()
                .userPk(auth != null ? auth.getPk() : 0l)
                .targetNickname(nickname)
                .build();
        UserInfoDTO.Response response = userService.getMemberInfo(req);
        return result(response.isSuccess(), response, HttpStatus.OK);
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResult<Object>> updateMemberInfo(
            @AuthenticationPrincipal JwtAuthentication auth,
            @RequestBody UserInfoUpdateDTO.Request req
    ) {
        req.setUserPk(auth != null ? auth.getPk() : 0l);
        UserInfoUpdateDTO.Response response = userService.updateMemberInfo(req);
        return result(response.isSuccess(), response, HttpStatus.OK);
    }

    @GetMapping("/alarm")
    public ResponseEntity<ApiResult<AlarmDTO.Response>> getUsersAlarm(
            @AuthenticationPrincipal JwtAuthentication auth
    ) {
        AlarmDTO.Request req = AlarmDTO.Request.builder()
                .userPk(auth != null ? auth.getPk() : 0l)
                .build();

        AlarmDTO.Response res = userService.getAlarm(req);
        return result(res.isSuccess(), res, HttpStatus.OK);
    }
}
