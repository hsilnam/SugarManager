package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENU")
@EntityListeners(value = AuditingEntityListener.class)
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_pk")
    private long pk;

    @Column(name = "user_pk")
    private long userPk;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Setter
    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}
