package kr.co.sugarmanager.business.challenge.repository;

import kr.co.sugarmanager.business.challenge.entity.ChallengeTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplateEntity, Long> {
    @Query("select t from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0")
    List<ChallengeTemplateEntity> findTodaysChallenges(@Param("day") int day);

    @Query("select distinct (t.userPk) from ChallengeTemplateEntity t where BITAND(t.days, :day) > 0")
    List<Long> findUsersWithChallenges (@Param("day") int day);
}

