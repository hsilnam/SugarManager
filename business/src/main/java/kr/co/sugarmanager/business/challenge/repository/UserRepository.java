package kr.co.sugarmanager.business.challenge.repository;

import kr.co.sugarmanager.business.challenge.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u.nickname from UserEntity u where u.pk = :userPk")
    String findNicknameById(@Param("userPk") Long userPk);
}
