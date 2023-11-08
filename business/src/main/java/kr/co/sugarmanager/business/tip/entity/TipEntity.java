package kr.co.sugarmanager.business.tip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "FAQ")
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}
