package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@RedisHash(value = "refresh", timeToLive = 1209600000l)
public class RefreshTokenEntity {
    private Long id;
    @Id
    private String refreshToken;
    private LocalDateTime createdAt;

    @Builder
    public RefreshTokenEntity(Long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
    }
}
