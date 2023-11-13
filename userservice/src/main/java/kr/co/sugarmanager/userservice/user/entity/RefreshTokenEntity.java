package kr.co.sugarmanager.userservice.user.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@RedisHash(value = "refresh", timeToLive = 1209600000l)
public class RefreshTokenEntity {
    @Id
    private String refreshToken;
    private Long userId;
    private LocalDateTime createdAt;

    @Builder
    public RefreshTokenEntity(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
    }
}
