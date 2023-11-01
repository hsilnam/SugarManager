package kr.co.sugarmanager.alarmchallenge.challenge.repository;

import kr.co.sugarmanager.alarmchallenge.challenge.entity.ChallengeTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplateEntity, Long> {

    @Query("select t from ChallengeTemplateEntity t where t.pk = :challenge")
    ChallengeTemplateEntity findByChallengeId(@Param("challenge") Long challenge);
}

