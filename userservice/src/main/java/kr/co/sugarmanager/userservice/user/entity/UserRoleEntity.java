package kr.co.sugarmanager.userservice.user.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.userservice.global.entity.CUDBaseEntity;
import kr.co.sugarmanager.userservice.user.vo.RoleType;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_ROLES")
@SQLDelete(sql = "UPDATE USER_ROLES SET DELETED_AT WHERE USER_ROLES_PK = ?")
@Where(clause = "DELETED_AT IS NULL")
public class UserRoleEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLES_PK")
    private long pk;

    @Column(name = "USER_ROLE")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    //Relation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PK")
    private UserEntity user;

    protected void setUser(UserEntity user) {
        this.user = user;
    }
}
