package kr.co.sugarmanager.userservice.user.repository;

import kr.co.sugarmanager.userservice.group.entity.GroupEntity;
import kr.co.sugarmanager.userservice.user.vo.SocialType;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<UserEntity> findByNickname(String nickname);

    @Query("select u,ui from " +
            "UserEntity u " +
            "left join fetch UserImageEntity ui on ui.user = u " +
            "join GroupEntity g on u.group = g " +
            "where g = :group")
    List<Object[]> findAllByGroup(@Param("group") GroupEntity group);
}
