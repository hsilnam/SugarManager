package kr.co.sugarmanager.userservice.group.service;

import kr.co.sugarmanager.userservice.group.dto.GroupCreateDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupJoinDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupLeaveDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupMemberListDTO;

public interface GroupService {
    GroupCreateDTO.Response createGroup(GroupCreateDTO.Request req);

    GroupLeaveDTO.Response leaveGroup(GroupLeaveDTO.Request req);

    GroupJoinDTO.Response joinGroup(GroupJoinDTO.Request req);

    GroupMemberListDTO.Response getGroupMembers(GroupMemberListDTO.Request req);
}
