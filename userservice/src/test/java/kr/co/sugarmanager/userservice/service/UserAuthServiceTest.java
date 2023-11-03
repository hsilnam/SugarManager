package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.entity.RefreshTokenEntity;
import kr.co.sugarmanager.userservice.entity.RoleType;
import kr.co.sugarmanager.userservice.entity.SocialType;
import kr.co.sugarmanager.userservice.entity.UserEntity;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.JwtExpiredException;
import kr.co.sugarmanager.userservice.exception.JwtValidationException;
import kr.co.sugarmanager.userservice.repository.RefreshTokenRepository;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class UserAuthServiceTest {
    private final String secret = "SECRETSECRETSECRETSECRETSECRETSECRET";
    private final String issuer = "TEST";
    private final long expired = 1000000l;
    private final long refreshExpired = 1000000l;
    private UserAuthService userAuthService;

    @Mock
    private KakaoOAuthService kakaoOAuthService;

    private JwtProvider jwtProvider = new JwtProvider(expired, refreshExpired, secret, issuer);
    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private final String accessToken = "accessToken";
    private final String fcmToken = "fcmToken";
    private final String IMAGE_URL = "IMAGE_URL";
    private final Long SOCIAL_PK = Long.MAX_VALUE;
    private final String EMAIL = "email@gmail.com";
    private List<UserEntity> userList = new ArrayList<>();
    private List<RefreshTokenEntity> refreshTokenList = new ArrayList<>();

    @BeforeEach
    public void initKakaoOAuthService() {
        userAuthService = new UserAuthServiceImpl(kakaoOAuthService, userRepository, jwtProvider, refreshTokenRepository);
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
        lenient().when(refreshTokenRepository.save(any())).thenAnswer(invocation -> {
            RefreshTokenEntity entity = invocation.getArgument(0, RefreshTokenEntity.class);
            refreshTokenList = refreshTokenList.stream().filter(ref -> !ref.getUserId().equals(entity.getUserId())).collect(Collectors.toList());
            refreshTokenList.add(entity);
            return entity;
        });
        lenient().when(refreshTokenRepository.findById(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0, String.class);
            return refreshTokenList.stream().filter(ref -> ref.getRefreshToken().equals(id)).findAny();
        });
    }

    @Nested
    @DisplayName("소셜 로그인")
    class SocialLoginTest {
        @Test
        @DirtiesContext
        public void 소셜_로그인_성공() {
            SocialLoginDTO.Response response = userAuthService.socialLogin(SocialLoginDTO.Request.builder()
                    .accessToken(accessToken)
                    .fcmToken(fcmToken)
                    .build());

            assertThat(response.getAccessToken()).isNotBlank();
            assertThat(response.getRefreshToken()).isNotBlank();

            Long id = jwtProvider.getClaims(response.getAccessToken(), "id", Long.class);
            List<String> roles = jwtProvider.getClaims(response.getAccessToken(), "roles", List.class);

            Optional<UserEntity> byId = userRepository.findById(Long.valueOf(id));
            assertThat(byId).isNotEmpty();
            UserEntity user = byId.get();

            assertThat(id).isEqualTo(user.getPk());
            assertThat(roles.size()).isEqualTo(1);
            assertThat(roles).contains(RoleType.MEMBER.getValue());
            assertThat(refreshTokenList.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("리프레쉬")
    class RefreshToken {
        long id = 1;
        List<String> roles = List.of(RoleType.MEMBER.getValue(), RoleType.ADMIN.getValue());
        Map<String, Object> payload = new HashMap<>();

        @BeforeEach
        public void init() {
            payload.put("id", id);
            payload.put("roles", roles);
        }

        @Test
        public void 리프레쉬_성공() {
            String refreshToken = jwtProvider.createRefreshToken(payload);
            RefreshDTO.Request req = RefreshDTO.Request.builder()
                    .refreshToken(refreshToken)
                    .build();
            refreshTokenRepository.save(RefreshTokenEntity.builder()
                    .refreshToken(refreshToken)
                    .userId(1l)
                    .build());

            RefreshDTO.Response response = userAuthService.refreshToken(req);
            assertThat(response.getAccessToken()).isNotEqualTo(accessToken);
            verify(refreshTokenRepository, times(1)).findById(anyString());
            assertThat(jwtProvider.getClaims(response.getAccessToken(), "id", Long.class)).isEqualTo(id);
            assertThat(jwtProvider.getClaims(response.getAccessToken(), "roles", List.class)).containsAll(roles);
        }

        @Test
        public void 리프레쉬_토큰_변조() {
            String refreshToken = "not.valid.token";

            RefreshDTO.Request req = RefreshDTO.Request.builder()
                    .refreshToken(refreshToken)
                    .build();

            JwtValidationException exception = assertThrows(JwtValidationException.class, () -> {
                userAuthService.refreshToken(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.JWT_BADREQUEST_EXCEPTION);
        }

        @Test
        @DirtiesContext
        public void 리프레시_토큰_만료() {
            jwtProvider = new JwtProvider(expired, 0, secret, issuer);

            String refreshToken = jwtProvider.createRefreshToken(payload);
            RefreshDTO.Request req = RefreshDTO.Request.builder()
                    .refreshToken(refreshToken)
                    .build();

            JwtExpiredException exception = assertThrows(JwtExpiredException.class, () -> {
                userAuthService.refreshToken(req);
            });
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.JWT_EXPIRED_EXCEPTION);
        }
    }
}
