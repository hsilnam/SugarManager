package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SETTING")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE SETTING SET DELETED_AT = NOW() WHERE SETTING_PK = ?")
@Where(clause = "DELETED_AT is null")
public class UserSettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SETTING_PK")
    private long pk;

    @Column(name = "FCM_TOKEN", nullable = false)
    private String fcmToken;

    @Column(name = "SETTING_CHALLENGE_ALERT", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private boolean challengeAlert = false;

    @Column(name = "SETTING_BLOOD_SUGAR_ALERT", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private boolean sugarAlert = false;

    @Column(name = "SETTING_BLOOD_SUGAR_HOUR")
    @ColumnDefault("1")
    @Builder.Default
    private int sugarAlertHour = 1;

    @Column(name = "user_pk")
    private long userPk;

}

