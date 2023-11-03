package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.UserInfoDTO;
import kr.co.sugarmanager.userservice.entity.UserEntity;
import kr.co.sugarmanager.userservice.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.UserNotFoundException;
import kr.co.sugarmanager.userservice.repository.UserRepository;
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
}
