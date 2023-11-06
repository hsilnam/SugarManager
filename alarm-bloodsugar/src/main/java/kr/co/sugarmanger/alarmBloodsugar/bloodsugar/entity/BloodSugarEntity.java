package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "BLOOD_SUGAR")
@SQLDelete(sql = "UPDATE BLOOD_SUGAR SET DELETED_AT = now() WHERE BLOOD_SUGAR_PK = ?")
public class BloodSugarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blood_sugar_pk")
    private long pk;

    @Column(name = "user_pk")
    private long userPk;

    @Column(name = "blood_sugar_content")
    private String content;

    @Column(name = "blood_sugar_level")
    private int level;

    @Column(name = "blood_sugar_category")
    private String category;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Setter
    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;
}

