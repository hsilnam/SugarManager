package kr.co.sugarmanager.userservice.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import kr.co.sugarmanager.userservice.user.dto.AlarmDTO;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.exception.InternalServerErrorException;
import kr.co.sugarmanager.userservice.global.entity.CUDBaseEntity;
import kr.co.sugarmanager.userservice.user.vo.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SETTING")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE SETTING SET DELETED_AT = NOW() WHERE SETTING_PK = ?")
@Where(clause = "DELETED_AT IS NULL")
@Slf4j
public class UserSettingEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SETTING_PK")
    private long pk;

    @Column(name = "FCM_TOKEN", nullable = false)
    private String fcmToken;

    @Column(name = "SETTING_POKE_ALERT", nullable = false)
    @ColumnDefault("false")
    private boolean pokeAlert;

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

    public List<AlarmDTO.AlarmInfo> getAlarmInfos() {
        return Arrays.stream(AlertType.values())
                .map(alertType -> {
                    try {
                        boolean value = (boolean) UserSettingEntity.class.getDeclaredField(alertType.getMember()).get(this);
                        return AlarmDTO.AlarmInfo.builder()
                                .category(alertType)
                                .status(value)
                                .build();
                    } catch (Exception e) {
                        if (log.isErrorEnabled()) {
                            log.error("[AlarmInfo Error] 매핑 도중 오류가 발생했습니다.", e);
                        }
                        throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION);
                    }
                }).collect(Collectors.toList());
    }
}
