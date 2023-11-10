package kr.co.sugarmanager.business.challenge.repository;

import kr.co.sugarmanager.business.challenge.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u.nickname from UserEntity u where u.pk = :userPk")
    String findNicknameById(@Param("userPk") Long userPk);

    @Query("select u.pk from UserEntity u where u.nickname = :nickname")
    Long findIdByNickname(@Param("nickname") String nickname);

    @Query("select case when coalesce(count(u1.nickname),0) > 1 then true else false end as check from UserEntity u1 where u1.groupPk = (select u.groupPk from UserEntity u where u.pk = :userPk) and u1.nickname = :nickname")
    Boolean inSameGroup(@Param("userPk") Long userPk, @Param("nickname") String nickname);
}
