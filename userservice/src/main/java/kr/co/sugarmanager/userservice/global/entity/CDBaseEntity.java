package kr.co.sugarmanager.userservice.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)
@Getter
public abstract class CDBaseEntity {
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}
