package kr.co.sugarmanager.business.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHALLENGE_LOG")
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_log_pk")
    private long pk;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "challenge_template_pk")
    private long challengeTemplatePk;

}
