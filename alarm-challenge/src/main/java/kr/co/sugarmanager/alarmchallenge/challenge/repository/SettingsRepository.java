package kr.co.sugarmanager.alarmchallenge.challenge.repository;

import kr.co.sugarmanager.alarmchallenge.challenge.entity.UserSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettingsRepository extends JpaRepository<UserSettingEntity, Long> {
    @Query("select s from UserSettingEntity s where s.userPk = :userPk")
    UserSettingEntity findSettingByUserId(@Param("userPk") Long userPK);
}
