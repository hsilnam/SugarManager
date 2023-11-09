package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.GroupCreateDTO;
import kr.co.sugarmanager.userservice.dto.GroupJoinDTO;
import kr.co.sugarmanager.userservice.dto.GroupLeaveDTO;
import kr.co.sugarmanager.userservice.dto.GroupMemberListDTO;

public interface GroupService {
    GroupCreateDTO.Response createGroup(GroupCreateDTO.Request req);

    GroupLeaveDTO.Response leaveGroup(GroupLeaveDTO.Request req);

    GroupJoinDTO.Response joinGroup(GroupJoinDTO.Request req);

    GroupMemberListDTO.Response getGroupMembers(GroupMemberListDTO.Request req);
}
