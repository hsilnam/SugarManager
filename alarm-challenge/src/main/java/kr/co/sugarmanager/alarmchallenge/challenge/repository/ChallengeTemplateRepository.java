package kr.co.sugarmanager.alarmchallenge.challenge.repository;

import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeLogEntity;
import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplateEntity, Long> {

    @Query("select t from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0 and t.hour = :hour and t.minute = :minute")
    List<ChallengeTemplateEntity> findChallenges(@Param("day") int day, @Param("hour")int hour, @Param("minute") int minute);

    @Query("select t from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0")
    List<ChallengeTemplateEntity> findTodaysChallenges(@Param("day") int day);

    @Query("select distinct (t.userPk) from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0")
    List<Long> findUsersWithChallenges (@Param("day") int day);
}

