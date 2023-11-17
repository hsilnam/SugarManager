package kr.co.sugarmanager.userservice.integ;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.service.KakaoOAuthService;
import kr.co.sugarmanager.userservice.global.util.JwtProvider;
import kr.co.sugarmanager.userservice.global.util.StringUtils;
import kr.co.sugarmanager.userservice.global.vo.KakaoProfile;
import kr.co.sugarmanager.userservice.user.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.user.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.user.entity.RefreshTokenEntity;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import kr.co.sugarmanager.userservice.user.entity.UserImageEntity;
import kr.co.sugarmanager.userservice.user.repository.RefreshTokenRepository;
import kr.co.sugarmanager.userservice.user.repository.UserRepository;
import kr.co.sugarmanager.userservice.user.vo.SocialType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static kr.co.sugarmanager.userservice.global.util.APIUtils.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAuthTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private KakaoOAuthService kakaoOAuthService;
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private List<RefreshTokenEntity> redisRefreshToken = new ArrayList<>();


    private Random random = new Random(System.currentTimeMillis());

    private final KakaoProfile profile = KakaoProfile.builder()
            .id(random.nextLong())
            .kakao_account(KakaoProfile.KakaoAccount.builder()
                    .email(StringUtils.generateRandomString(10).concat("@").concat(StringUtils.generateRandomString(5)))
                    .profile(KakaoProfile.KakaoAccount.Profile.builder()
                            .profile_image_url("image_url")
                            .build())
                    .build())
            .properties(KakaoProfile.Properties.builder()
                    .profile_image("image_url")
                    .build())
            .build();

    @BeforeEach
    public void init() {
        lenient().when(refreshTokenRepository.save(ArgumentMatchers.any(RefreshTokenEntity.class)))
                .thenAnswer(invocation -> {
                    RefreshTokenEntity entity = invocation.getArgument(0, RefreshTokenEntity.class);

                    redisRefreshToken = redisRefreshToken.stream()
                            .filter(r -> !r.getRefreshToken().equals(entity.getRefreshToken()))
                            .collect(Collectors.toList());
                    redisRefreshToken.add(entity);
                    return entity;
                });
        lenient().when(refreshTokenRepository.findById(anyString()))
                .thenAnswer(invocation -> {
                    String id = invocation.getArgument(0, String.class);
                    return redisRefreshToken.stream()
                            .filter(r -> r.getRefreshToken().equals(id))
                            .findAny();
                });
        lenient().when(kakaoOAuthService.getUserInfoWithMobile(anyString()))
                .thenReturn(profile);
    }

    @Nested
    class SocialLoginTest {
        String accessToken = "accessToken";
        String fcmToken = "fcmToken";

        SocialLoginDTO.Request req = SocialLoginDTO.Request.builder()
                .accessToken(accessToken)
                .fcmToken(fcmToken)
                .build();

        @Test
        public void 첫_로그인() throws Exception {
            mvc.perform(
                            post("/api/v1/auth/kakao")
                                    .header("Content-Type", "application/json")
                                    .characterEncoding(StandardCharsets.UTF_8)
                                    .content(mapper.writeValueAsString(req)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.response.accessToken").isString())
                    .andExpect(jsonPath("$.response.refreshToken").isString())
                    .andExpect(jsonPath("$.error").isEmpty());

            Optional<UserEntity> user = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, Long.toString(profile.getId()));
            assertThat(user).isNotEmpty();
            UserEntity userEntity = user.get();
            assertAll("entity assert",
                    () -> assertThat(userEntity.getSocialId()).isEqualTo(Long.toString(profile.getId())),
                    () -> assertThat(userEntity.getEmail()).isEqualTo(profile.getKakao_account().getEmail()),
                    () -> assertThat(userEntity.getUserImage().getImageUrl()).isEqualTo(profile.getProperties().getProfile_image())
            );
        }

        @Test
        public void 이미지_변환_후_로그인() throws Exception {
            UserEntity user = UserEntity.builder()
                    .socialId(Long.toString(profile.getId()))
                    .socialType(SocialType.KAKAO)
                    .name("name")
                    .nickname("nickname")
                    .email(profile.getKakao_account().getEmail())
                    .build();
            UserImageEntity userImage = UserImageEntity.builder()
                    .imageUrl("beforeUrl")
                    .build();
            user.addProfileImage(userImage);
            userRepository.saveAndFlush(user);
            int beforeSize = userRepository.findAll().size();

            mvc.perform(post("/api/v1/auth/kakao")
                            .header("Content-Type", "application/json")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error").isEmpty())
                    .andExpect(jsonPath("$.response.accessToken").isString())
                    .andExpect(jsonPath("$.response.refreshToken").isString());
            userRepository.flush();
            Optional<UserEntity> user1 = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, Long.toString(profile.getId()));

            assertThat(user1).isNotEmpty();
            UserEntity userEntity = user1.get();
            assertAll("entity",
                    () -> assertThat(userEntity.getSocialId()).isEqualTo(Long.toString(profile.getId())),
                    () -> assertThat(userEntity.getUserImage().getImageUrl()).isEqualTo(profile.getProperties().getProfile_image()),
                    () -> assertThat(userEntity.getName()).isEqualTo(user.getName()),
                    () -> assertThat(userEntity.getNickname()).isEqualTo(user.getNickname())
            );

            assertThat(userRepository.findAll().size()).isEqualTo(beforeSize);
        }
    }

    @Nested
    class RefreshTokenTest {
        private String accessToken;
        private String refreshToken;

        private RefreshDTO.Request req;
        private Map<String, Object> payload;

        @BeforeEach
        public void tokenInit() {
            payload = new HashMap<>();
            payload.put("id", 1);
            payload.put("roles", "roles");
            accessToken = jwtProvider.createToken(payload);
            refreshToken = jwtProvider.createRefreshToken(payload);

            req = RefreshDTO.Request.builder()
                    .refreshToken(refreshToken)
                    .build();
        }

        @Test
        public void 리프레시_성공() throws Exception {
            redisRefreshToken.add(RefreshTokenEntity.builder()
                    .refreshToken(req.getRefreshToken())
                    .build());

            MvcResult result = mvc.perform(post("/api/v1/auth/refresh")
                            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.response.accessToken").isString())
                    .andExpect(jsonPath("$.error").isEmpty())
                    .andReturn();
            String content = result.getResponse().getContentAsString();
            ApiResult apiResult = mapper.readValue(content, ApiResult.class);
            RefreshDTO.Response response = mapper.convertValue(apiResult.getResponse(), RefreshDTO.Response.class);
            String rAccessToken = response.getAccessToken();
            assertDoesNotThrow(() -> jwtProvider.validateToken(rAccessToken));
        }

        @Test
        public void 변조된_리프레시_토큰_요청() throws Exception {
            req.setRefreshToken("변조된 리프레시 토큰");
            mvc.perform(post("/api/v1/auth/refresh")
                            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.response", nullValue()))
                    .andExpect(jsonPath("$.error.code", is(ErrorCode.JWT_BADREQUEST_EXCEPTION.getCode())))
                    .andExpect(jsonPath("$.error.message", is(ErrorCode.JWT_BADREQUEST_EXCEPTION.getMessage())));
        }

        @Test
        @DirtiesContext
        public void 만료된_리프레시_요청() throws Exception {
            jwtProvider = new JwtProvider(0, 0, jwtSecret, jwtIssuer);

            req.setRefreshToken(jwtProvider.createRefreshToken(payload));

            mvc.perform(post("/api/v1/auth/refresh")
                            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.response", nullValue()))
                    .andExpect(jsonPath("$.error.message", is(ErrorCode.JWT_EXPIRED_EXCEPTION.getMessage())))
                    .andExpect(jsonPath("$.error.code", is(ErrorCode.JWT_EXPIRED_EXCEPTION.getCode())));
        }

        @Test
        public void 만료된_리프레시_요청2() throws Exception {
            mvc.perform(post("/api/v1/auth/refresh")
                            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.response", nullValue()))
                    .andExpect(jsonPath("$.error.message", is(ErrorCode.UNAUTHORIZATION_EXCEPTION.getMessage())))
                    .andExpect(jsonPath("$.error.code", is(ErrorCode.UNAUTHORIZATION_EXCEPTION.getCode())));
        }
    }
}
