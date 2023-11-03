package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)
@Getter
public abstract class CUDBaseEntity {
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletdAt;
}
