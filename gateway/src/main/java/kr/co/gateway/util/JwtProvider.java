package kr.co.gateway.util;

import io.jsonwebtoken.*;
import kr.co.gateway.exception.ErrorCode;
import kr.co.gateway.exception.JwtExpiredException;
import kr.co.gateway.exception.JwtValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtProvider {
    public enum TokenType {
        ACCESS("accessToken"), REFRESH("refreshToken");
        private String type;

        TokenType(String type) {
            this.type = type;
        }

        String getType() {
            return this.type;
        }
    }

    private final long expired;

    private final long refreshExpired;

    private final String secret;
    private final String issuer;

    public JwtProvider(@Value("${jwt.expired}") long expired,
                       @Value("${jwt.refresh-expired}") long refreshExpired,
                       @Value("${jwt.secret}") String secret,
                       @Value("${jwt.issuer}") String issuer) {
        this.expired = expired;
        this.refreshExpired = refreshExpired;
        this.secret = secret;
        this.issuer = issuer;
    }

    public String getSubject(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException(ErrorCode.JWT_EXPIRED_EXCEPTION);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new JwtValidationException(ErrorCode.JWT_BADREQUEST_EXCEPTION);
        }
    }

    public <T> T getClaims(String token, String key, Class<T> valueType) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .get(key, valueType);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException(ErrorCode.JWT_EXPIRED_EXCEPTION);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new JwtValidationException(ErrorCode.JWT_BADREQUEST_EXCEPTION);
        }
    }

    public void validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException(ErrorCode.JWT_EXPIRED_EXCEPTION);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new JwtValidationException(ErrorCode.JWT_BADREQUEST_EXCEPTION);
        }
    }
}
