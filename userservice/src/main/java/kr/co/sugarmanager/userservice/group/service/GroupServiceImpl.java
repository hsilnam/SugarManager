package kr.co.sugarmanager.userservice.group.service;

import kr.co.sugarmanager.userservice.group.dto.GroupCreateDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupJoinDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupLeaveDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupMemberListDTO;
import kr.co.sugarmanager.userservice.group.entity.GroupEntity;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.exception.GroupNotFoundException;
import kr.co.sugarmanager.userservice.global.exception.GroupNotJoinException;
import kr.co.sugarmanager.userservice.global.exception.UserNotFoundException;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import kr.co.sugarmanager.userservice.user.entity.UserImageEntity;
import kr.co.sugarmanager.userservice.group.repository.GroupRepository;
import kr.co.sugarmanager.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GroupCreateDTO.Response createGroup(GroupCreateDTO.Request req) {
        long userPk = req.getUserPk();
        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (user.getGroup() != null) {
            return GroupCreateDTO.Response.builder()
                    .success(false)
                    .build();
        }

        GroupEntity group = GroupEntity.builder()
                .build();

        groupRepository.save(group);

        user.joinGroup(group);

        return GroupCreateDTO.Response.builder()
                .success(true)
                .groupCode(group.getGroupCode())
                .build();
    }

    @Override
    @Transactional
    public GroupLeaveDTO.Response leaveGroup(GroupLeaveDTO.Request req) {
        long userPk = req.getUserPk();

        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (user.getGroup() == null) {
            throw new GroupNotJoinException(ErrorCode.GROUP_NOT_JOIN_EXCEPTION);
        }

        int groupMemberCount = groupRepository.getGroupMember(user.getGroup().getGroupCode());

        GroupEntity exitedGroup = user.exitGroup();
        if (groupMemberCount == 1) {//내가 남은 멤버라면 같이 삭제
            groupRepository.delete(exitedGroup);
        }

        return GroupLeaveDTO.Response.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public GroupJoinDTO.Response joinGroup(GroupJoinDTO.Request req) {
        long userPk = req.getUserPk();
        String groupCode = req.getGroupCode();

        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (user.getGroup() != null) {
            return GroupJoinDTO.Response.builder()
                    .success(false)
                    .build();
        }

        //그룹이 존재하지 않으면 throw
        GroupEntity group = groupRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException(ErrorCode.GROUP_NOT_FOUND_EXCEPTION));

        user.joinGroup(group);

        return GroupJoinDTO.Response.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupMemberListDTO.Response getGroupMembers(GroupMemberListDTO.Request req) {
        long userPk = req.getUserPk();

        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        return GroupMemberListDTO.Response.builder()
                .success(true)
                .users(
                        userRepository.findAllByGroup(user.getGroup()).stream()
                                .map(obj -> {
                                    UserEntity usr = (UserEntity) obj[0];
                                    UserImageEntity usrImage = (UserImageEntity) obj[1];
                                    return GroupMemberListDTO.UserInfo.builder()
                                            .uid(usr.getPk())
                                            .nickname(usr.getNickname())
                                            .profileUrl(usrImage.getImageUrl())
                                            .build();
                                }).collect(Collectors.toList())
                )
                .build();
    }
}
