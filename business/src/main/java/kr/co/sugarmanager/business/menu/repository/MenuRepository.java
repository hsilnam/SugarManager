package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.dto.MenuPeriodInterface;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Query("SELECT e FROM MenuEntity e WHERE e.userPk = :userPk AND YEAR(e.registedAt) = :year AND MONTH(e.registedAt) = :month AND DAY(e.registedAt) = :day")
    List<MenuEntity> findByUserPkAndRegistedAt(@Param("userPk") Long userPk, @Param("year") int year, @Param("month") int month, @Param("day") int day);

    @Query("SELECT " +
            "  DATE(m.registedAt) AS time, " +
            "  SUM(f.foodCal) AS dayFoodCal, " +
            "  SUM(f.foodSugars) AS dayFoodSugars, " +
            "  SUM(f.foodProtein) AS dayFoodProtein, " +
            "  SUM(f.foodCarbohydrate) AS dayFoodCarbohydrate, " +
            "  SUM(f.foodFat) AS dayFoodFat " +
            "FROM MenuEntity m " +
            "LEFT JOIN m.foodList f " +
            "WHERE m.userPk = :userPk AND m.registedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(m.registedAt) " +
            "ORDER BY DATE(m.registedAt) DESC")
    Page<MenuPeriodInterface> findByPeriod(@Param("userPk") Long userPk, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, PageRequest pageRequest);

    @Query("select distinct(cast (m.registedAt as localdate)) from MenuEntity m where year(m.registedAt) = :year and month(m.registedAt) = :month and m.userPk = :searchUserPk")
    List<LocalDate> findMenuRecordsForMonth(@Param("searchUserPk") Long searchUserPk, @Param("year") Integer year, @Param("month") Integer month);

    @Query("select m from MenuEntity m where m.userPk = :searchUserPk and m.registedAt between :start and :end")
    List<MenuEntity> findMenuRecordsForDay(@Param("searchUserPk") Long searchUserPk, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
