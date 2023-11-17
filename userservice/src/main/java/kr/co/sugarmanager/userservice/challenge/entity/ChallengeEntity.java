package kr.co.sugarmanager.userservice.challenge.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.userservice.global.entity.CUDBaseEntity;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "CHALLENGE_TEMPLATE")
@SQLDelete(sql = "UPDATE CHALLENGE_TEMPLATE SET DELETED_AT = NOW() WHERE CHALLENGE_TEMPLATE_PK = ?")
@Where(clause = "DELETED_AT IS NULL")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHALLENGE_TEMPLATE_PK")
    private long pk;

    @Column(name = "CHALLENGE_TITLE")
    private String title;

    @Column(name = "CHALLENGE_GOAL")
    private String goal;

    @Column(name = "CHALLENGE_TYPE")
    private String type;

    @Column(name = "CHALLENGE_ALERT")
    private boolean alert;

    @Column(name = "CHALLENGE_ALERT_HOUR")
    private int alertHour;

    @Column(name = "CHALLENGE_ALERT_MIN")
    private int alertMin;

    @Column(name = "CHALLENGE_ALERT_DAYS")
    private int alertDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PK")
    private UserEntity user;
}
