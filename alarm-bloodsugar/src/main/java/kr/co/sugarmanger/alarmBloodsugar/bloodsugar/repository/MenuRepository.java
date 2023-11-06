package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository;


import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    @Query("select m from MenuEntity m where m.created_at = (select max(m1.created_at) from MenuEntity m1 where m1.userPk = :userPk and m1.created_at between :startOfDay and :endOfDay)")
    MenuEntity findLatestMenu(@Param("userPk") long userPk, @Param("startOfDay")LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
