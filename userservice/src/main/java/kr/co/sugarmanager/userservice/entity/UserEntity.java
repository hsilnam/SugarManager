package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import kr.co.sugarmanager.userservice.dto.UserInfoUpdateDTO;
import kr.co.sugarmanager.userservice.exception.ValidationException;
import lombok.*;
import org.hibernate.annotations.*;

import java.util.*;

import static kr.co.sugarmanager.userservice.exception.ErrorCode.*;
import static kr.co.sugarmanager.userservice.entity.UserInfoValidation.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USERS")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON USER WHERE USER_PK = ?")
@Where(clause = "DELETED_AT IS NOT NULL")
@ToString
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

    @Column(name = "USER_SOCIAL_TYPE", length = 10)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "USER_SOCIAL_ID")
    private String socialId;

    @Column(name = "BLOOD_SUGAR_MAX")
    private int sugarMax;

    @Column(name = "BLOOD_SUGAR_MIN")
    private int sugarMin;

    //Relation
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
    private UserSettingEntity setting;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "GROUP_PK")
    private GroupEntity group;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserImageEntity userImage;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserRoleEntity> roles = new HashSet<>();

    //custom methods
    public void addRoles(Set<UserRoleEntity> roles) {
        this.roles = roles;
        roles.stream().forEach(role -> role.setUser(this));
    }

    public void addSetting(UserSettingEntity setting) {
        this.setting = setting;
        setting.setUser(this);
    }

    public void addProfileImage(UserImageEntity userImage) {
        this.userImage = userImage;
        userImage.setUser(this);
    }

    public GroupEntity joinGroup(GroupEntity group) {
        return this.group = group;
    }

    public GroupEntity exitGroup() {
        GroupEntity group = this.group;
        this.group = null;
        return group;
    }

    public void updateInfo(UserInfoUpdateDTO.Request dto) {
        setName(dto.getName());
        setNickname(dto.getNickname());
        setBirthday(dto.getBirthday());
        setHeight(dto.getHeight());
        setWeight(dto.getWeight());
        setSugarMin(dto.getBloodSugarMin());
        setSugarMax(dto.getBloodSugarMax());
        setGender(dto.getGender());
    }

    //setter
    private void setNickname(String nickname) {
        //6~20자
        if (!NICKNAME.validate(nickname)) {
            throw new ValidationException(NICKNAME_NOT_VALID_EXCEPTION);
        }
        this.nickname = nickname;
    }

    private void setHeight(Integer height) {
        if (!HEIGHT.validate(height)) {
            throw new ValidationException(HEIGHT_NOT_VALID_EXCEPTION);
        }
        this.height = height;
    }

    private void setWeight(Integer weight) {
        if (!WEIGHT.validate(weight)) {
            throw new ValidationException(WEIGHT_NOT_VALID_EXCEPTION);
        }
        this.weight = weight;
    }

    private void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
    }

    private void setSugarMin(int sugarMin) {
        this.sugarMin = sugarMin;
    }

    private void setName(String name) {
        if (!NAME.validate(name)) {
            throw new ValidationException(NAME_NOT_VALID_EXCEPTION);
        }
        this.name = name;
    }

    private void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    private void setGender(String gender) {
        if (!GENDER.validate(gender)) {
            throw new ValidationException(GENDER_NOT_VALID_EXCEPTION);
        }
        this.gender = gender.equalsIgnoreCase("male") ? false : true;
    }

    private void setGender(Boolean gender) {
        this.gender = gender;
    }

    private void setUserImage(UserImageEntity userImage) {
        this.userImage = userImage;
    }
}
