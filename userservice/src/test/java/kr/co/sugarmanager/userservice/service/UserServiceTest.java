package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.UserInfoDTO;
import kr.co.sugarmanager.userservice.entity.*;
import kr.co.sugarmanager.userservice.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.UserNotFoundException;
import kr.co.sugarmanager.userservice.repository.UserRepository;
import kr.co.sugarmanager.userservice.util.JwtProvider;
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
import static org.mockito.Mockito.lenient;

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
        private String accessToken;
        private UserEntity owner;
        private List<UserEntity> userEntityList = new ArrayList<>();
        private List<GroupEntity> groupEntityList = new ArrayList<>();

        @BeforeEach
        public void initMember() {
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
}
