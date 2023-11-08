package kr.co.sugarmanager.alarmchallenge.challenge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHALLENGE_TEMPLATE")
@EntityListeners(value = AuditingEntityListener.class)
@DynamicUpdate
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeTemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_template_pk")
    private long pk;

    @Column(name = "user_pk")
    private long userPk;

    @Column(name = "challenge_title")
    private String title;

    @Column(name = "challenge_goal")
    private int goal;

    @Column(name = "challenge_type")
    private String type;

    @Column(name = "challenge_alert")
    private boolean alert;

    @Column(name = "challenge_alert_hour")
    private int hour;

    @Column(name = "challenge_alert_min")
    private int minute;

    @Column(name = "challenge_alert_days")
    private int days;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


}
