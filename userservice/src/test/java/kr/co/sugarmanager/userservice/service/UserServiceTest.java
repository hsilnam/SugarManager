package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.UserInfoDTO;
import kr.co.sugarmanager.userservice.dto.UserInfoUpdateDTO;
import kr.co.sugarmanager.userservice.entity.*;
import kr.co.sugarmanager.userservice.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.UserNotFoundException;
import kr.co.sugarmanager.userservice.exception.ValidationException;
import kr.co.sugarmanager.userservice.repository.UserRepository;
import kr.co.sugarmanager.userservice.util.JwtProvider;
import kr.co.sugarmanager.userservice.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private long expired = 1000000l;
    private long refreshExpired = 10000000l;
    private String secret = "SECRETSECRETSECRETSECRETSECRETSECRETSECRET";
    private String issuer = "TESTER";
    private JwtProvider jwtProvider = new JwtProvider(expired, refreshExpired, secret, issuer);

    private List<UserEntity> userList;

    private String accessToken;
    private UserEntity owner;
    private List<UserEntity> userEntityList = new ArrayList<>();
    private List<GroupEntity> groupEntityList = new ArrayList<>();

    @BeforeEach
    public void init() {
        userList = new ArrayList<>();
        lenient().when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0, UserEntity.class);

            Optional<UserEntity> any = userList.stream().filter(u -> u.getPk() == entity.getPk()).findAny();
            if (any.isEmpty()) {
                Field pk = UserEntity.class.getDeclaredField("pk");
                pk.setAccessible(true);
                pk.set(entity, userList.size() + 1);
                pk.setAccessible(false);
                userList.add(entity);
                return entity;
            } else {
                userList = userList.stream().filter(u -> u.getPk() != entity.getPk()).collect(Collectors.toList());
                userList.add(entity);
                return entity;
            }
        });
        lenient().when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            long pk = invocation.getArgument(0, Long.class);

            return userList.stream().filter(u -> u.getPk() == pk)
                    .findAny();
        });
        lenient().when(userRepository.findByNickname(anyString())).thenAnswer(invocation -> {
            String nickname = invocation.getArgument(0, String.class);

            return userList.stream().filter(u -> u.getNickname().equals(nickname))
                    .findAny();
        });

        int n = 100;
        int groupCount = 10;
        for (int i = 0; i < groupCount; i++) {
            GroupEntity groupEntity = GroupEntity.builder()
                    .groupCode(Integer.toString(i))
                    .build();

            groupEntityList.add(groupEntity);
        }

        for (int i = 0; i < n; i++) {
            UserEntity user = UserEntity.builder()
                    .name("tester".concat(Integer.toString(i)))
                    .nickname("tester".concat(Integer.toString(i)))
                    .email("tester".concat(Integer.toString(i).concat("@gmail.com")))
                    .socialType(SocialType.KAKAO)
                    .socialId(Integer.toString(i))
                    .group(groupEntityList.get(i % groupEntityList.size()))
                    .build();

            UserImageEntity image = UserImageEntity.builder()
                    .imageUrl("image".concat(Integer.toString(i)))
                    .build();

            UserSettingEntity setting = UserSettingEntity.builder()
                    .fcmToken("fcmToken")
                    .sugarAlertHour(i % n + 1)
                    .sugarAlert(i % 2 == 0 ? true : false)
                    .pokeAlert(i % 2 == 0 ? false : true)
                    .challengeAlert(i % 2 == 0 ? true : false)
                    .build();

            UserRoleEntity role = UserRoleEntity.builder()
                    .role(RoleType.MEMBER)
                    .build();

            user.addProfileImage(image);
            user.addSetting(setting);
            user.addRoles(Set.of(role));
            userEntityList.add(user);
            userRepository.save(user);
        }

        owner = userList.get(0);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", owner.getPk());
        payload.put("roles", owner.getRoles().stream().map(r -> r.getRole().getValue()).collect(Collectors.toList()));
        accessToken = jwtProvider.createToken(payload);
    }

    @Nested
    @DisplayName("정보 조회")
    class MemberInfo {
        /**
         * 정상 조회(내것)
         * 정상 조회(그룹것)
         * 비정상 조회(내그룹이 아닌 남의것)
         * 비정상 조회(없는 user의 token으로 접근 )
         * 비정상 조회(없는 닉네임으로 조회)
         */

        @Test
        public void 내것_정상_조회() {
            UserInfoDTO.Response memberInfo = userService.getMemberInfo(UserInfoDTO.Request.builder()
                    .targetNickname(owner.getNickname())
                    .userPk(owner.getPk())
                    .build());

            assertResponse(owner, memberInfo);
        }

        @Test
        public void 그룹원_정상_조회() {
            UserEntity target = userList.stream()
                    .filter(u -> u.getGroup().getGroupCode().equals(owner.getGroup().getGroupCode()))
                    .findAny().get();

            UserInfoDTO.Response memberInfo = userService.getMemberInfo(UserInfoDTO.Request.builder()
                    .userPk(owner.getPk())
                    .targetNickname(target.getNickname())
                    .build());

            assertResponse(target, memberInfo);
        }

        @Test
        public void 내그룹이_아닌_남의것_조회() {
            UserEntity target = userList.stream()
                    .filter(u -> !u.getGroup().getGroupCode().equals(owner.getGroup().getGroupCode()))
                    .findAny().get();

            AccessDenyException exception = assertThrows(AccessDenyException.class, () -> {
                userService.getMemberInfo(UserInfoDTO.Request.builder()
                        .targetNickname(target.getNickname())
                        .userPk(owner.getPk())
                        .build());
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        @Test
        public void 없는_유저의_토큰으로_접근() {
            UserEntity target = userList.get(0);

            AccessDenyException exception = assertThrows(AccessDenyException.class, () -> {
                userService.getMemberInfo(UserInfoDTO.Request.builder()
                        .targetNickname(target.getNickname())
                        .userPk(Long.MAX_VALUE)
                        .build());
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZATION_EXCEPTION);
        }

        @Test
        public void 없는_닉네임_조회() {
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                userService.getMemberInfo(UserInfoDTO.Request.builder()
                        .userPk(owner.getPk())
                        .targetNickname("unknown!")
                        .build());
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        }


        private void assertResponse(UserEntity entity, UserInfoDTO.Response response) {
            assertAll("assert response",
                    () -> assertThat(response.getUid()).isEqualTo(entity.getPk()),
                    () -> assertThat(response.getName()).isEqualTo(entity.getName()),
                    () -> assertThat(response.getProfileImage()).isEqualTo(entity.getUserImage().getImageUrl()),
                    () -> assertThat(response.getGender()).isEqualTo(entity.getGender() == null ? null : entity.getGender() ? "여자" : "남자"),
                    () -> assertThat(response.getEmail()).isEqualTo(entity.getEmail()),
                    () -> assertThat(response.getBirthday()).isEqualTo(entity.getBirthday()),
                    () -> assertThat(response.getGroupCode()).isEqualTo(entity.getGroup().getGroupCode()),
                    () -> assertThat(response.getHeight()).isEqualTo(entity.getHeight()),
                    () -> assertThat(response.getWeight()).isEqualTo(entity.getWeight()),
                    () -> assertThat(response.getBloodSugarMin()).isEqualTo(entity.getSugarMin()),
                    () -> assertThat(response.getBloodSugarMax()).isEqualTo(entity.getSugarMax()));
        }
    }

    @Nested
    @DisplayName("정보 업데이트")
    public class UpdateUserInfo {
        UserInfoUpdateDTO.Request req = null;
        UserEntity user = null;

        @BeforeEach
        public void init() {
            user = userEntityList.get(0);
            req = UserInfoUpdateDTO.Request.builder()
                    .userPk(user.getPk())
                    .birthday(new Date())
                    .bloodSugarMax(10)
                    .bloodSugarMin(9)
                    .height(170)
                    .weight(70)
                    .nickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMin() + 1))
                    .name(StringUtils.generateRandomString(UserInfoValidation.NAME.getMin() + 1))
                    .gender("MALE")
                    .build();
        }

        @Test
        public void 유저_정보_업데이트_성공() {
            UserInfoUpdateDTO.Response response = userService.updateMemberInfo(req);
            assertThat(response.isSuccess()).isTrue();
            verify(userRepository, times(1)).findByNickname(anyString());
            verify(userRepository, times(1)).findById(anyLong());
            assertAfterUpdate(user.getPk(), true);
        }


        @Test
        public void 닉네임_중복_실패() {
            String duplicatedNickname = userEntityList.get(1).getNickname();
            req.setNickname(duplicatedNickname);

            UserInfoUpdateDTO.Response response = userService.updateMemberInfo(req);
            assertThat(response.isSuccess()).isFalse();
        }

        @Test
        public void 닉네임_길이_최소_불만족() {
            req.setNickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMin() - 1));

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 닉네임_길이_최대_불만족() {
            req.setNickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMax() + 1));

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 이름_길이_최소_불만족() {
            req.setName(StringUtils.generateRandomString(UserInfoValidation.NAME.getMin() - 1));

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NAME_NOT_VALID_EXCEPTION);
            assertAfterUpdate(user.getPk(), false);
        }

        @Test
        public void 이름_길이_최대_불만족() {
            req.setName(StringUtils.generateRandomString(UserInfoValidation.NAME.getMax() + 1));

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NAME_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 신장_최소_불만족() {
            req.setHeight(UserInfoValidation.HEIGHT.getMin() - 1);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HEIGHT_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 신장_최대_불만족() {
            req.setHeight(UserInfoValidation.HEIGHT.getMax() + 1);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.HEIGHT_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 체중_최소_불만족() {
            req.setWeight(UserInfoValidation.WEIGHT.getMin() - 1);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.WEIGHT_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 체중_최대_불만족() {
            req.setWeight(UserInfoValidation.WEIGHT.getMax() + 1);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.WEIGHT_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 성별_불만족() {
            req.setGender(StringUtils.generateRandomString(4));

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.GENDER_NOT_VALID_EXCEPTION);
        }

        @Test
        public void 없는_유저로_접근시_실패() {
            req.setUserPk(Long.MAX_VALUE);

            AccessDenyException exception = assertThrows(AccessDenyException.class, () -> {
                userService.updateMemberInfo(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZATION_EXCEPTION);
        }

        private void assertAfterUpdate(long userPk, boolean success) {
            UserEntity updated = userRepository.findById(userPk).get();
            if (success) {
                assertAll("update success!",
                        () -> assertThat(updated.getBirthday()).isEqualTo(req.getBirthday()),
                        () -> assertThat(updated.getNickname()).isEqualTo(req.getNickname()),
                        () -> assertThat(updated.getName()).isEqualTo(req.getName()),
                        () -> assertThat(updated.getHeight()).isEqualTo(req.getHeight()),
                        () -> assertThat(updated.getWeight()).isEqualTo(req.getWeight()),
                        () -> assertThat(updated.getSugarMin()).isEqualTo(req.getBloodSugarMin()),
                        () -> assertThat(updated.getSugarMax()).isEqualTo(req.getBloodSugarMax())
                );
            } else {
                assertAll("update success!",
                        () -> assertThat(updated.getBirthday()).isNotEqualTo(req.getBirthday()),
                        () -> assertThat(updated.getNickname()).isNotEqualTo(req.getNickname()),
                        () -> assertThat(updated.getName()).isNotEqualTo(req.getName()),
                        () -> assertThat(updated.getHeight()).isNotEqualTo(req.getHeight()),
                        () -> assertThat(updated.getWeight()).isNotEqualTo(req.getWeight()),
                        () -> assertThat(updated.getSugarMin()).isNotEqualTo(req.getBloodSugarMin()),
                        () -> assertThat(updated.getSugarMax()).isNotEqualTo(req.getBloodSugarMax())
                );
            }
        }
    }
}
