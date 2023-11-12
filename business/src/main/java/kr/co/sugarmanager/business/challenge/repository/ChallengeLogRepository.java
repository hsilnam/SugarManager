package kr.co.sugarmanager.business.challenge.repository;

import kr.co.sugarmanager.business.challenge.dto.LogsAndLatestInterface;
import kr.co.sugarmanager.business.challenge.entity.ChallengeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ChallengeLogRepository extends JpaRepository<ChallengeLogEntity, Long> {

    @Query("select count(c) " +
            "from ChallengeLogEntity c " +
            "left join ChallengeTemplateEntity t on c.challengeTemplatePk = t.pk " +
            "where " +
            "c.createdAt between :start and :end and c.challengeTemplatePk = :challengePk")
    Integer findChallengeLogs(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("challengePk") Long challengePk);

    @Query("select coalesce(count(c.pk),0) as count, MAX(c.createdAt) as latest from ChallengeLogEntity c left join ChallengeTemplateEntity t on c.challengeTemplatePk = t.pk where c.createdAt between :start and :end and c.challengeTemplatePk = :challengePk ")
    LogsAndLatestInterface findChallengeLogsAndLatestUpdatedTime(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("challengePk") Long challengePk);
}

