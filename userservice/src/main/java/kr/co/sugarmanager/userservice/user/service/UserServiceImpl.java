package kr.co.sugarmanager.userservice.user.service;

import kr.co.sugarmanager.userservice.challenge.entity.ChallengeEntity;
import kr.co.sugarmanager.userservice.global.dto.MessageDTO;
import kr.co.sugarmanager.userservice.global.exception.*;
import kr.co.sugarmanager.userservice.global.service.ProducerService;
import kr.co.sugarmanager.userservice.user.dto.*;
import kr.co.sugarmanager.userservice.user.vo.PokeType;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import kr.co.sugarmanager.userservice.user.entity.UserSettingEntity;
import kr.co.sugarmanager.userservice.challenge.repository.ChallengeRepository;
import kr.co.sugarmanager.userservice.user.repository.UserRepository;
import kr.co.sugarmanager.userservice.user.repository.UserSettingRepository;
import kr.co.sugarmanager.userservice.global.util.StringUtils;
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
    private final ChallengeRepository challengeRepository;
    private final UserSettingRepository userSettingRepository;
    private final ProducerService producerService;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public UserInfoDTO.Response getMemberInfo(UserInfoDTO.Request req) {
        long userPk = req.getUserPk();
        String targetNickname = req.getTargetNickname();

        UserEntity owner = userRepository.findById(userPk)
                .orElseThrow(() -> new AccessDenyException(ErrorCode.UNAUTHORIZATION_EXCEPTION));

        UserEntity target = null;
        if (StringUtils.isBlank(targetNickname)) {
            target = owner;
        } else {
            target = userRepository.findByNickname(targetNickname)
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        }
        if (owner.getPk() == target.getPk()
                || (owner.getGroup() != null && target.getGroup() != null
                && owner.getGroup().getGroupCode().equals(target.getGroup().getGroupCode()))) {
            return UserInfoDTO.Response.builder()
                    .success(true)
                    .uid(target.getPk())
                    .name(target.getName())
                    .nickname(target.getNickname())
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
        long userPk = req.getUserPk();
        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new AccessDenyException(ErrorCode.UNAUTHORIZATION_EXCEPTION));
        //중복 닉네임 검사
        if (userRepository.findByNicknameExclude(req.getNickname(), user.getNickname()).isPresent()) {
            return UserInfoUpdateDTO.Response.builder()
                    .success(false)
                    .build();
        }
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
                .bloodSugarHour(userSettingEntity.getSugarAlertHour())
                .build();
    }

    @Override
    @Transactional
    public AlarmUpdateDTO.Response setAlarm(AlarmUpdateDTO.Request req) {
        long count = userSettingRepository.setAlarm(req.getUserPk(), req);
        if (count == 0) {//업데이트 한 칼럼이 존재하지 않음
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        } else if (count > 1) {//혹시모르게 2개이상의 칼럼이 업데이트되었다면, 정합성 오류기때문에 error throw하며 rollback
            throw new InternalServerErrorException(ErrorCode.SQL_EXCEPTION);
        }

        return AlarmUpdateDTO.Response.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PokeDTO.Response poke(PokeDTO.Request req) {
        long userPk = req.getUserPk();
        String nickname = req.getNickname();
        PokeType type = req.getType();
        if (type == null) {
            throw new ValidationException(ErrorCode.CATEGORY_NOT_VALID_EXCEPTION);
        }
        Long challengeId = req.getChallengeId();
        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        UserEntity target = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        //찌르기 권한이 있다면
        if (user.getGroup() != null && target.getGroup() != null
                && user.getGroup().getGroupCode().equals(target.getGroup().getGroupCode())) {
            //상대가 poke에 대한 알람을 설정했으면 go
            if (target.getSetting().isPokeAlert()) {
                StringBuffer titleBuffer = new StringBuffer();
                StringBuffer bodyBuffer = new StringBuffer();
                titleBuffer.append(user.getNickname()).append(" 님의 찌르기입니다.");
                switch (type) {
                    case CHALLENGE:
                        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                                .orElseThrow(() -> new ChallengeNotFoundException(ErrorCode.CHALLENGE_NOT_FOUND_EXCEPTION));
                        bodyBuffer.append(challenge.getTitle());
                        break;
                    case BLOODSUGAR:
                        bodyBuffer.append("혈당을 체크하세요!");
                        break;
                    default:
                        throw new ValidationException(ErrorCode.POKE_CATERORY_NOT_VALID_EXCEPTION);
                }

                //kafka에 메세지 넣기
                producerService.sendMessage(MessageDTO.builder()
                        .fcmToken(target.getSetting().getFcmToken())
                        .title(titleBuffer.toString())
                        .body(bodyBuffer.toString())
                        .build());
                return PokeDTO.Response.builder()
                        .success(true)
                        .nickname(target.getNickname())
                        .challengeId(challengeId)
                        .type(type)
                        .build();
            } else {
                return PokeDTO.Response.builder()
                        .success(false)
                        .build();
            }
        } else {
            throw new AccessDenyException(ErrorCode.FORBIDDEN_EXCEPTION);
        }
    }
}
