package kr.co.sugarmanager.userservice.entity;

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
@SQLDelete(sql = "UPDATE SET DELETED_AT = NOW() ON SETTING WHERE SETTING_PK = ?")
public class UserSettingEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SETTING_PK")
    private long pk;

    @Column(name = "FCM_TOKEN", nullable = false)
    private String fcmToken;

    @Column(name = "SETTING_POKE_ALERT", nullable = false)
    @ColumnDefault("false")
    private boolean pokeAlert = false;

    @Column(name = "SETTING_CHALLENGE_ALERT", nullable = false)
    @ColumnDefault("false")
    private boolean challengeAlert;

    @Column(name = "SETTING_BLOOD_SUGAR_ALERT", nullable = false)
    @ColumnDefault("false")
    private boolean sugarAlert;

    @Column(name = "SETTING_BLOOD_SUGAR_HOUR")
    @ColumnDefault("1")
    @Builder.Default
    private int sugarAlertHour = 1;

    //Relation
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_PK")
    private UserEntity user;

    //Methods
    protected void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean toggleAlert(AlertType type) {
        switch (type) {
            case POKE:
                this.pokeAlert = !this.pokeAlert;
                return this.pokeAlert;
            case CHALLENGE:
                this.challengeAlert = !this.challengeAlert;
                return this.challengeAlert;
            case BLOOD:
                this.sugarAlert = !this.sugarAlert;
                return this.sugarAlert;
            default:
                return false;
        }
    }
}
