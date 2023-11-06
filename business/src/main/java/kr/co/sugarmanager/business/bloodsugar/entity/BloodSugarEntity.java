package kr.co.sugarmanager.business.bloodsugar.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE BLOOD_SUGAR SET DELETED_AT = now() WHERE MENU_PK = ?")
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
}
