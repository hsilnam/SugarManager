package kr.co.sugarmanager.userservice.repository;

import kr.co.sugarmanager.userservice.entity.SocialType;
import kr.co.sugarmanager.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<UserEntity> findByNickname(String nickname);
}
