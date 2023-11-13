package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.entity.RoleType;
import kr.co.sugarmanager.userservice.entity.SocialType;
import kr.co.sugarmanager.userservice.entity.UserEntity;
import kr.co.sugarmanager.userservice.repository.UserRepository;
import kr.co.sugarmanager.userservice.util.JwtProvider;
import kr.co.sugarmanager.userservice.vo.KakaoProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserAuthServiceTest {
    private UserAuthService userAuthService;

    @Mock
    private KakaoOAuthService kakaoOAuthService;

    private JwtProvider jwtProvider = new JwtProvider(1000000l, 1000000l, "SECRETSECRETSECRETSECRETSECRETSECRET", "TEST");
    @Mock
    private UserRepository userRepository;

    private final String accessToken = "accessToken";
    private final String fcmToken = "fcmToken";
    private final String IMAGE_URL = "IMAGE_URL";
    private final Long SOCIAL_PK = Long.MAX_VALUE;
    private final String EMAIL = "email@gmail.com";
    private List<UserEntity> userList = new ArrayList<>();

    @BeforeEach
    public void initKakaoOAuthService() {
        userAuthService = new UserAuthServiceImpl(kakaoOAuthService, userRepository, jwtProvider);
        lenient().when(kakaoOAuthService.getUserInfo(anyString())).thenAnswer(invocation ->
                KakaoProfile.builder()
                        .id(SOCIAL_PK)
                        .properties(KakaoProfile.Properties.builder()
                                .profile_image(IMAGE_URL)
                                .build())
                        .kakao_account(KakaoProfile.KakaoAccount.builder()
                                .email(EMAIL)
                                .build())
                        .build()
        );
        lenient().when(kakaoOAuthService.getUserInfoWithMobile(anyString())).thenAnswer(invocation ->
                KakaoProfile.builder()
                        .id(SOCIAL_PK)
                        .properties(KakaoProfile.Properties.builder()
                                .profile_image(IMAGE_URL)
                                .build())
                        .kakao_account(KakaoProfile.KakaoAccount.builder()
                                .email(EMAIL)
                                .build())
                        .build()
        );
        lenient().when(userRepository.findBySocialTypeAndSocialId(any(SocialType.class), any(String.class)))
                .thenAnswer(invocation -> {
                    SocialType socialType = invocation.getArgument(0, SocialType.class);
                    String socialId = invocation.getArgument(1, String.class);
                    Optional<UserEntity> any = userList.stream().filter(e -> e.getSocialId().equals(socialId) && e.getSocialType().name().equals(socialType.name())).findAny();
                    return any;
                });
        lenient().when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            long id = invocation.getArgument(0, Long.class);
            return userList.stream().filter(u -> u.getPk() == id).findAny();
        });
        lenient().when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0, UserEntity.class);

            Optional<UserEntity> any = userList.stream().filter(u -> u.getPk() != 0 && u.getPk() == entity.getPk()).findAny();

            if (any.isEmpty()) {
                Field pk = UserEntity.class.getDeclaredField("pk");
                pk.setAccessible(true);
                pk.set(entity, userList.size() + 1);
                pk.setAccessible(false);
                userList.add(entity);
                return entity;
            } else {
                UserEntity origin = any.get();
                origin.getUserImage().setImageUrl(entity.getUserImage().getImageUrl());
                return origin;
            }
        });
    }

    @Nested
    @DisplayName("소셜 로그인")
    class SocialLoginTest {
        @Test
        public void 소셜_로그인_성공() {
            SocialLoginDTO.Response response = userAuthService.socialLogin(SocialLoginDTO.Request.builder()
                    .accessToken(accessToken)
                    .fcmToken(fcmToken)
                    .build());

            assertThat(response.getAccessToken()).isNotBlank();
            assertThat(response.getRefreshToken()).isNotBlank();

            Integer id = jwtProvider.getClaims(response.getAccessToken(), "id", Integer.class);
            List<String> roles = jwtProvider.getClaims(response.getAccessToken(), "roles", List.class);

            Optional<UserEntity> byId = userRepository.findById(Long.valueOf(id));
            assertThat(byId).isNotEmpty();
            UserEntity user = byId.get();

            assertThat(id).isEqualTo(user.getPk());
            assertThat(roles.size()).isEqualTo(1);
            assertThat(roles).contains(RoleType.MEMBER.getValue());
        }
    }
}
