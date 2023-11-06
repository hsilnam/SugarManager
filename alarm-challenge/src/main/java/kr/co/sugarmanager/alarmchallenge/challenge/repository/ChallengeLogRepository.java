package kr.co.sugarmanager.alarmchallenge.challenge.repository;

import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeLogRepository extends JpaRepository<ChallengeLogEntity, Long> {

    @Query("select c " +
            "from ChallengeLogEntity c " +
                "left join ChallengeTemplateEntity t on c.challengeTemplatePk = t.pk " +
            "where " +
                "c.createdAt between :start and :end and t.hour = :hour and t.minute = :minute")
    List<ChallengeLogEntity> findAllChallenges(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("hour")int hour, @Param("minute") int minute);

}
