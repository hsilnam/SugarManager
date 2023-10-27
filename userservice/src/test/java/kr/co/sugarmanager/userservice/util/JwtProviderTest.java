package kr.co.sugarmanager.userservice.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes=JwtProvider.class)
class JwtProviderTest {
    @Value("${jwt.expired}")
    private long expired;
    @Value("${jwt.refresh-expired}")
    private long refreshExpired;
    @Autowired
    private JwtProvider provider;

    @Nested
    @DisplayName("Create Token Test")
    class CreateTokenTest{
        @Nested
        @DisplayName("success")
        class Success{

            @Test
            public void 어세스_토큰_생성_성공(){
                Map<String,Object> payload = new HashMap<>();
                String[] keys = {"key1","key2","key3"};
                String[] values = {"value1","value2","value3"};
                for(int i=0;i<keys.length;i++){
                    payload.put(keys[i],values[i]);
                }

                String token = provider.createToken(payload);
                assertThat(token).isNotBlank();

                String subject = provider.getSubject(token);
                assertThat(subject).isEqualTo(JwtProvider.TokenType.ACCESS.getType());

                for(int i=0;i<keys.length;i++){
                    String value = provider.getClaims(token,keys[i],String.class);
                    assertThat(value).isEqualTo(values[i]);
                }

                int issuedAt = provider.getClaims(token,"iat",Integer.class);
                int exp = provider.getClaims(token,"exp",Integer.class);

                assertThat(exp-issuedAt).isEqualTo(expired/1000);

                assertThat(provider.validateToken(token)).isTrue();
            }

            @Test
            public void 리프레쉬_토큰_생성_성공(){
                String refreshToken = provider.createRefreshToken();

                assertThat(refreshToken).isNotBlank();

                assertThat(provider.getSubject(refreshToken)).isEqualTo(JwtProvider.TokenType.REFRESH.getType());

                int issuedAt = provider.getClaims(refreshToken,"iat",Integer.class);
                int exp = provider.getClaims(refreshToken,"exp",Integer.class);

                assertThat(exp-issuedAt).isEqualTo(refreshExpired/1000);

                assertThat(provider.validateToken(refreshToken)).isTrue();
            }
        }
    }
}