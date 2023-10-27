package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USER")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON USER WHERE USER_PK = ?")
public class UserEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_PK", updatable = false)
    private long pk;

    @Column(name = "USER_ID", unique = true, length = 320)
    private String id;

    @Column(name = "USER_PW", length = 200)
    private String pw;

    @Column(name = "USER_EMAIL", unique = true, length = 320)
    private String email;

    @Column(name = "USER_NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "USER_NICKNAME", nullable = false, length = 20)
    private String nickname;

    @Column(name = "USER_HEIGHT")
    private Integer height;

    @Column(name = "USER_WEIGHT")
    private Integer weight;

    @Column(name = "USER_BIRTHDAY")
    private Date birthday;

    @Column(name = "USER_GENDER")
    private Boolean gender;

    @Column(name = "USER_KAKAO", nullable = false)
    private boolean kakao;

    @Column(name = "USER_KAKAO_PK")
    private Long kakaoPk;

    @Column(name = "BLOOD_SUGAR_MAX")
    private int sugarMax;

    @Column(name = "BLOOD_SUGAR_MIN")
    private int sugarMin;

    //Relation
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
    private UserSettingEntity setting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_PK")
    private GroupEntity group;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserImageEntity userImage;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserRoleEntity> roles = new ArrayList<>();
}
