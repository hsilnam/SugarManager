package kr.co.sugarmanager.userservice.entity;


import jakarta.persistence.*;
import kr.co.sugarmanager.userservice.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

import java.util.Random;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "GROUP")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON GROUP WHERE GROUP_PK = ?")
public class GroupEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_PK")
    private long pk;

    @Column(name = "GROUP_CODE", nullable = false, length = 10)
    private String groupCode;

    public String generateGroupCode() {
        return StringUtils.generateRandomString(10);
    }
}
