package kr.co.sugarmanager.business.challenge.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@SQLDelete(sql = "UPDATE CHALLENGE_TEMPLATE SET DELETED_AT = now() WHERE CHALLENGE_TEMPLATE_PK = ?")
@Where(clause = "DELETED_AT is null")
public class ChallengeTemplateEntity extends CUDEntity {
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
    private Integer hour;

    @Column(name = "challenge_alert_min")
    private Integer minute;

    @Column(name = "challenge_alert_days")
    private int days;

}
