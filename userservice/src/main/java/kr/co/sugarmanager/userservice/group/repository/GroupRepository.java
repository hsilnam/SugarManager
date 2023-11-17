package kr.co.sugarmanager.userservice.group.repository;

import kr.co.sugarmanager.userservice.group.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    @Query("select " +
            "case count(g) " +
            "when 0 then false " +
            "else true " +
            "end " +
            "from GroupEntity g where g.groupCode = :groupCode")
    boolean existsGroupCode(@Param("groupCode") String groupCode);

    @Query("select count(g) from " +
            "GroupEntity g " +
            "join fetch UserEntity u on u.group = g " +
            "where g.groupCode = :groupCode")
    int getGroupMember(@Param("groupCode") String groupCode);

    Optional<GroupEntity> findByGroupCode(String groupCode);
}
