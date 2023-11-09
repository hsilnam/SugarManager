package kr.co.sugarmanager.business.tip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FAQ")
public class TipEntity {
    @Id
    @Column(name = "FAQ_PK")
    private long pk;

    @Column(name = "FAQ_TITLE")
    private String title;

    @Column(name = "FAQ_CONTENT")
    private String content;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}
