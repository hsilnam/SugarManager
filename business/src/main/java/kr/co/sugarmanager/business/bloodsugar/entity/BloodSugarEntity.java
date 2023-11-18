package kr.co.sugarmanager.business.bloodsugar.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.bloodsugar.exception.BloodSugarException;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE BLOOD_SUGAR SET DELETED_AT = now() WHERE BLOOD_SUGAR_PK = ?")
@Where(clause = "DELETED_AT is null")
@Getter
@Setter
@ToString
@Table(name = "BLOOD_SUGAR")
@Entity
public class BloodSugarEntity extends CUDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOOD_SUGAR_PK")
    private Long bloodSugarPk;
    @Column(name = "USER_PK")
    private Long userPk;
    @Column(name = "BLOOD_SUGAR_CATEGORY")
    private String category;
    @Column(name = "BLOOD_SUGAR_LEVEL")
    private int level;
    @Column(name = "BLOOD_SUGAR_CONTENT")
    private String content;
    @Column(name = "REGISTED_AT")
    @Builder.Default
    private LocalDateTime registedAt = LocalDateTime.now();

    @Builder
    public BloodSugarEntity(Long userPk, String category, int level, String content) {
        this.userPk = userPk;
        this.category = category;
        setLevel(level);
        this.level = level;
        this.content = content;
    }

    public void setLevel(int level) {
        if (level <= 0 || level > 500) {
            throw new BloodSugarException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.level = level;
    }
}
