package kr.co.sugarmanager.business.challenge.repository;

import kr.co.sugarmanager.business.challenge.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select case when coalesce(count(u.nickname),0) >= 1 then true else false end as check from UserEntity u where u.pk = :userPk")
    Boolean isAuthorized(@Param("userPk") Long userPk);

    @Query("select u.nickname from UserEntity u where u.pk = :userPk")
    Optional<String> findNicknameById(@Param("userPk") Long userPk);

    @Query("select u.pk from UserEntity u where u.nickname = :nickname")
    Optional<Long> findIdByNickname(@Param("nickname") String nickname);

    @Query("select " +
            "case when coalesce(count(u1.nickname),0) > 0 then true else false end as checkgroup " +
            "from UserEntity u1 " +
            "where u1.groupPk = (select u.groupPk from UserEntity u where u.pk = :userPk) and u1.nickname = :nickname")
    Boolean inSameGroup(@Param("userPk") Long userPk, @Param("nickname") String nickname);

    @Query("select u.groupPk from UserEntity u where u.nickname = :nickname")
    Optional<Integer> findGroupIdByNickname(@Param("nickname") String nickname);
}
