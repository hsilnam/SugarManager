package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.AlarmDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoUpdateDTO;
import kr.co.sugarmanager.userservice.entity.UserEntity;
import kr.co.sugarmanager.userservice.entity.UserSettingEntity;
import kr.co.sugarmanager.userservice.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.UserNotFoundException;
import kr.co.sugarmanager.userservice.repository.UserRepository;
import kr.co.sugarmanager.userservice.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public UserInfoDTO.Response getMemberInfo(UserInfoDTO.Request req) {
        long userPk = req.getUserPk();
        String targetNickname = req.getTargetNickname();

        UserEntity owner = userRepository.findById(userPk)
                .orElseThrow(() -> new AccessDenyException(ErrorCode.UNAUTHORIZATION_EXCEPTION));

        UserEntity target = userRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (owner.getPk() == target.getPk()
                || (owner.getGroup() != null && target.getGroup() != null
                && owner.getGroup().getGroupCode().equals(target.getGroup().getGroupCode()))) {
            return UserInfoDTO.Response.builder()
                    .success(true)
                    .uid(target.getPk())
                    .name(target.getName())
                    .email(target.getEmail())
                    .gender(target.getGender() == null ? null : target.getGender() ? "여자" : "남자")
                    .birthday(target.getBirthday())
                    .height(target.getHeight())
                    .weight(target.getWeight())
                    .bloodSugarMin(target.getSugarMin())
                    .bloodSugarMax(target.getSugarMax())
                    .profileImage(target.getUserImage() != null ? target.getUserImage().getImageUrl() : null)
                    .groupCode(target.getGroup() != null ? target.getGroup().getGroupCode() : null)
                    .build();
        } else {
            throw new AccessDenyException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)//닉네임 중복을 피하기 위해 SERIALIZABLE
    public UserInfoUpdateDTO.Response updateMemberInfo(UserInfoUpdateDTO.Request req) {
        //중복 닉네임 검사
        if (userRepository.findByNickname(req.getNickname()).isPresent()) {
            return UserInfoUpdateDTO.Response.builder()
                    .success(false)
                    .build();
        }
        long userPk = req.getUserPk();
        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new AccessDenyException(ErrorCode.UNAUTHORIZATION_EXCEPTION));
        user.updateInfo(req);

        return UserInfoUpdateDTO.Response.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AlarmDTO.Response getAlarm(AlarmDTO.Request req) {
        long pk = req.getUserPk();
        UserSettingEntity userSettingEntity = userSettingRepository.findByUser(UserEntity.builder()
                        .pk(pk)
                        .build())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        return AlarmDTO.Response.builder()
                .success(true)
                .alarms(userSettingEntity.getAlarmInfos())
                .build();
    }
}
