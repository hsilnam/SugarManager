package kr.co.sugarmanager.userservice.repository;

import kr.co.sugarmanager.userservice.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
}
