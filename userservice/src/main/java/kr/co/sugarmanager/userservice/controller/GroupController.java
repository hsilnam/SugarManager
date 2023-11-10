package kr.co.sugarmanager.userservice.controller;

import static kr.co.sugarmanager.userservice.util.APIUtils.*;

import kr.co.sugarmanager.userservice.dto.GroupCreateDTO;
import kr.co.sugarmanager.userservice.dto.GroupJoinDTO;
import kr.co.sugarmanager.userservice.dto.GroupLeaveDTO;
import kr.co.sugarmanager.userservice.dto.GroupMemberListDTO;
import kr.co.sugarmanager.userservice.service.GroupService;
import kr.co.sugarmanager.userservice.util.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<ApiResult<GroupCreateDTO.Response>> createGroup(
            @AuthenticationPrincipal JwtAuthentication auth
    ) {
        GroupCreateDTO.Request req = GroupCreateDTO.Request.builder()
                .userPk(auth != null ? auth.getPk() : 0l)
                .build();
        GroupCreateDTO.Response res = groupService.createGroup(req);
        return result(res.isSuccess(), res, HttpStatus.CREATED);
    }

    @PostMapping("/leave")
    public ResponseEntity<ApiResult<GroupLeaveDTO.Response>> leaveGroup(
            @AuthenticationPrincipal JwtAuthentication auth
    ) {
        GroupLeaveDTO.Request req = GroupLeaveDTO.Request.builder()
                .userPk(auth != null ? auth.getPk() : 0l)
                .build();

        GroupLeaveDTO.Response res = groupService.leaveGroup(req);
        return result(res.isSuccess(), res, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResult<GroupJoinDTO.Response>> joinGroup(
            @AuthenticationPrincipal JwtAuthentication auth,
            @RequestBody GroupJoinDTO.Request req
    ) {
        req.setUserPk(auth != null ? auth.getPk() : 0l);
        GroupJoinDTO.Response res = groupService.joinGroup(req);
        return result(res.isSuccess(), res, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResult<GroupMemberListDTO.Response>> getGroup(
            @AuthenticationPrincipal JwtAuthentication auth,
            @RequestBody GroupMemberListDTO.Request req
    ) {
        req.setUserPk(auth != null ? auth.getPk() : 0l);
        GroupMemberListDTO.Response res = groupService.getGroupMembers(req);
        return result(res.isSuccess(), res, HttpStatus.OK);
    }

}
