package kr.co.sugarmanager.userservice.global.util;

import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.exception.JwtExpiredException;
import kr.co.sugarmanager.userservice.global.exception.JwtValidationException;
import kr.co.sugarmanager.userservice.global.util.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = JwtProvider.class)
class JwtProviderTest {
    @Value("${jwt.expired}")
    private long expired;
    @Value("${jwt.refresh-expired}")
    private long refreshExpired;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.issuer}")
    private String issuer;

    @Autowired
    private JwtProvider provider;

    @Nested
    @DisplayName("Create Token Test")
    class CreateTokenTest {
        @Nested
        @DisplayName("success")
        class Success {

            @Test
            public void 어세스_토큰_생성_성공() {
                Map<String, Object> payload = new HashMap<>();
                String[] keys = {"key1", "key2", "key3"};
                String[] values = {"value1", "value2", "value3"};
                for (int i = 0; i < keys.length; i++) {
                    payload.put(keys[i], values[i]);
                }

                String token = provider.createToken(payload);
                assertThat(token).isNotBlank();

                String subject = provider.getSubject(token);
                assertThat(subject).isEqualTo(JwtProvider.TokenType.ACCESS.getType());

                for (int i = 0; i < keys.length; i++) {
                    String value = provider.getClaims(token, keys[i], String.class);
                    assertThat(value).isEqualTo(values[i]);
                }

                long issuedAt = provider.getClaims(token, "iat", Date.class).getTime();
                long exp = provider.getClaims(token, "exp", Date.class).getTime();

                assertThat(exp - issuedAt).isEqualTo(expired);

                assertDoesNotThrow(() -> {
                    provider.validateToken(token);
                });
            }

            @Test
            public void 리프레쉬_토큰_생성_성공() {
                String refreshToken = provider.createRefreshToken();

                assertThat(refreshToken).isNotBlank();

                assertThat(provider.getSubject(refreshToken)).isEqualTo(JwtProvider.TokenType.REFRESH.getType());

                long issuedAt = provider.getClaims(refreshToken, "iat", Date.class).getTime();
                long exp = provider.getClaims(refreshToken, "exp", Date.class).getTime();

                assertThat(exp - issuedAt).isEqualTo(refreshExpired);

                assertDoesNotThrow(() -> {
                    provider.validateToken(refreshToken);
                });
            }
        }

        @Nested
        @DisplayName("fail")
        class Fail {
            Map<String, Object> payload = new HashMap<>();
            String[] keys = {"key1", "key2", "key3"};
            String[] values = {"value1", "value2", "value3"};

            @BeforeEach
            public void init() {
                //accessToken의 payload 초기화
                for (int i = 0; i < keys.length; i++) {
                    payload.put(keys[i], values[i]);
                }
            }

            @Test
            public void 어세스_토큰_만료() {
                //accessToken의 만료시간을 0초로 조정
                JwtProvider newProvider = new JwtProvider(0, refreshExpired, secret, issuer);

                String token = newProvider.createToken(payload);

                JwtExpiredException expiredExceptionCode = assertThrows(JwtExpiredException.class, () -> {
                    newProvider.validateToken(token);
                });

                expiredExceptionCode = assertThrows(JwtExpiredException.class, () -> {
                    newProvider.getClaims(token, keys[0], String.class);
                });
                assertThat(expiredExceptionCode.getErrorCode()).isEqualTo(ErrorCode.JWT_EXPIRED_EXCEPTION);

                expiredExceptionCode = assertThrows(JwtExpiredException.class, () -> {
                    newProvider.getSubject(token);
                });
                assertThat(expiredExceptionCode.getErrorCode()).isEqualTo(ErrorCode.JWT_EXPIRED_EXCEPTION);
            }

            @Test
            public void 리프레쉬_토큰_만료() {
                //accessToken의 만료시간을 0초로 조정
                JwtProvider newProvider = new JwtProvider(0, refreshExpired, secret, issuer);

                String token = newProvider.createToken(payload);

                JwtExpiredException expiredExceptionCode = assertThrows(JwtExpiredException.class, () -> {
                    newProvider.validateToken(token);
                });
                expiredExceptionCode = assertThrows(JwtExpiredException.class, () -> {
                    newProvider.getSubject(token);
                });
                assertThat(expiredExceptionCode.getErrorCode()).isEqualTo(ErrorCode.JWT_EXPIRED_EXCEPTION);
            }

            @Test
            public void 어세스_토큰_변조() {
                String token = provider.createToken(payload);
                String[] split = token.split("\\.");

                split[1] = Base64.getEncoder().encodeToString("{\"change\":true}".getBytes());//payload 변조

                final String modifiedToken = Arrays.stream(split).collect(Collectors.joining("."));//payload 변조 부분을 재 삽입

                JwtValidationException jwtValidationException = assertThrows(JwtValidationException.class, () -> {
                    provider.validateToken(modifiedToken);
                });
                assertThat(jwtValidationException.getErrorCode()).isEqualTo(ErrorCode.JWT_BADREQUEST_EXCEPTION);

                JwtValidationException customJwtException = assertThrows(JwtValidationException.class, () -> {
                    provider.getSubject(modifiedToken);
                });

                assertThat(customJwtException.getErrorCode()).isEqualTo(ErrorCode.JWT_BADREQUEST_EXCEPTION);
            }

            @Test
            public void 리프레쉬_토큰_변조() {
                String refreshToken = provider.createRefreshToken();
                String[] split = refreshToken.split("\\.");

                split[1] = Base64.getEncoder().encodeToString("{\"sub\":\"accessToken\"}".getBytes());//payload 변조

                final String modifiedToken = Arrays.stream(split).collect(Collectors.joining("."));//payload 변조 부분을 재 삽입

                JwtValidationException jwtValidationException = assertThrows(JwtValidationException.class, () -> {
                    provider.validateToken(modifiedToken);
                });
                assertThat(jwtValidationException.getErrorCode()).isEqualTo(ErrorCode.JWT_BADREQUEST_EXCEPTION);

                JwtValidationException customJwtException = assertThrows(JwtValidationException.class, () -> {
                    provider.getSubject(modifiedToken);
                });

                assertThat(customJwtException.getErrorCode()).isEqualTo(ErrorCode.JWT_BADREQUEST_EXCEPTION);
            }
        }

    }
}