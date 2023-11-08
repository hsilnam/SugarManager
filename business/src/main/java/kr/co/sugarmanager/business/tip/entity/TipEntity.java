package kr.co.sugarmanager.business.tip.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "FAQ")
@EntityListeners(value = AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE FAQ SET DELETED_AT = now() WHERE FAQ_PK = ?")
@Where(clause = "DELETED_AT is null")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FAQ_PK")
    private long pk;

    @Column(name = "FAQ_TITLE")
    private String title;

    @Column(name = "FAQ_CONTENT")
    private String content;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}
