package kr.co.sugarmanager.business.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SETTING")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE SETTING SET DELETED_AT = now() WHERE SETTING_PK = ?")

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

    @Column(name = "USER_PK")
    private long userPk;

}

