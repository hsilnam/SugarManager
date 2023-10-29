package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_ROLES")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON USER_ROLES WHERE USER_ROLES_PK = ?")
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

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
