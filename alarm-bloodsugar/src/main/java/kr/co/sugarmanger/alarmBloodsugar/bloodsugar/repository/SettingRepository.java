package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.repository;

import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.entity.UserSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<UserSettingEntity, Long> {
    @Query("select s from UserSettingEntity s where s.sugarAlert = true")
    List<UserSettingEntity> findAlertUserList();

}

