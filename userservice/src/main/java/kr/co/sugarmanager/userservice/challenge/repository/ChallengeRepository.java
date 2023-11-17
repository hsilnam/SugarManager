package kr.co.sugarmanager.userservice.challenge.repository;

import kr.co.sugarmanager.userservice.challenge.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
}
