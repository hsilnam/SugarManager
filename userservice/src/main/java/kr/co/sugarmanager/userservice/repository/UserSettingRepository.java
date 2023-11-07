package kr.co.sugarmanager.userservice.repository;

import kr.co.sugarmanager.userservice.entity.UserEntity;
import kr.co.sugarmanager.userservice.entity.UserSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingRepository
        extends JpaRepository<UserSettingEntity, Long>, CustomAlertRepository {
    Optional<UserSettingEntity> findByUser(UserEntity user);
}
