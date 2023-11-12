package kr.co.sugarmanager.userservice.global.util;

import io.jsonwebtoken.*;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.exception.JwtExpiredException;
import kr.co.sugarmanager.userservice.global.exception.JwtValidationException;
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

    public String refreshToken(String refreshToken) {
        Claims claims = this.getClaims(refreshToken);
        return createToken(claims);
    }

    public String createRefreshToken(Map<String, Object> payload) {
        return createToken(TokenType.REFRESH, payload, refreshExpired);
    }

    public String createToken(Claims payload) {
        return createToken(TokenType.ACCESS, payload, refreshExpired);
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

    public String createToken(TokenType subject, Claims payload, long expired) {
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

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
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
