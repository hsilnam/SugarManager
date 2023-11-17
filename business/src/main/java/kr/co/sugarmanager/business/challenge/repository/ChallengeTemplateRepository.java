package kr.co.sugarmanager.business.challenge.repository;

import kr.co.sugarmanager.business.challenge.entity.ChallengeTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplateEntity, Long> {
    @Query("select t from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0")
    Optional<List<ChallengeTemplateEntity>> findTodaysChallenges(@Param("day") int day);

    @Query("select distinct (t.userPk) from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0")
    Optional<List<Long>> findUsersWithChallenges (@Param("day") int day);

    @Query("select t from ChallengeTemplateEntity t where t.userPk = :userPk")
    Optional<List<ChallengeTemplateEntity>> findAllChallengesByUser(@Param("userPk") Long userPk);

    @Query("select t from ChallengeTemplateEntity t where t.pk = :challengePk")
    Optional<ChallengeTemplateEntity> findChallengeByPk(@Param("challengePk") Long challengePk);
}

