package kr.co.sugarmanager.userservice.util;

import io.jsonwebtoken.*;
import kr.co.sugarmanager.userservice.exception.CustomJwtException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider {
    enum TokenType {
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


    public String createRefreshToken() {
        return createToken(TokenType.REFRESH, null, refreshExpired);
    }

    public String createToken(Map<String, Object> payload) {
        return createToken(TokenType.ACCESS, payload, expired);
    }

    public String createToken(TokenType subject, Map<String, Object> payload, long expired) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject.getType())
                .addClaims(payload)
                .setExpiration(new Date(now.getTime() + expired))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8))
                .setIssuedAt(now)
                .setIssuer(issuer)
                .compact();
    }

    public String getSubject(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(ErrorCode.JWT_EXPIRED_EXCEPTION);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new CustomJwtException(ErrorCode.UNAUTHORIZATION_EXCEPTION);
        }
    }

    public <T> T getClaims(String token, String key, Class<T> valueType) {
        try {
            return (T) Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .get(key);
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(ErrorCode.JWT_EXPIRED_EXCEPTION);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new CustomJwtException(ErrorCode.UNAUTHORIZATION_EXCEPTION);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            return false;
        } catch (MalformedJwtException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
