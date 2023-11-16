package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    Optional<MenuEntity> findByMenuPkAndUserPk(Long menuPk, Long userPk);

    @Query("select distinct(cast (m.registedAt as localdate)) from MenuEntity m where year(m.registedAt) = :year and month(m.registedAt) = :month and m.userPk = :searchUserPk")
    List<LocalDate> findMenuRecordsForMonth(@Param("searchUserPk") Long searchUserPk, @Param("year") Integer year, @Param("month") Integer month);

    @Query("select m from MenuEntity m where m.userPk = :searchUserPk and m.registedAt between :start and :end")
    List<MenuEntity> findMenuRecordsForDay(@Param("searchUserPk") Long searchUserPk, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
