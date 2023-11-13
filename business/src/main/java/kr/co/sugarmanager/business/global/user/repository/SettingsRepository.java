package kr.co.sugarmanager.business.global.user.repository;

import kr.co.sugarmanager.business.global.user.entity.UserSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SettingsRepository extends JpaRepository<UserSettingEntity, Long> {
    @Query("select s from UserSettingEntity s where s.userPk = :userPk")
    UserSettingEntity findSettingByUserId(@Param("userPk") Long userPK);
    @Query("select s from UserSettingEntity s where s.challengeAlert = true")
    List<UserSettingEntity> findUsersWithChallengeAlarmOn();

    @Query("select s.pokeAlert from UserSettingEntity s where s.userPk = :userPk")
    Boolean isPokeAlarm(@Param("userPk") Long userPk);
}

