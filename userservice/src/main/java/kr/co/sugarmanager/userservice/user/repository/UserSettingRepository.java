package kr.co.sugarmanager.userservice.user.repository;

import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import kr.co.sugarmanager.userservice.user.entity.UserSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingRepository
        extends JpaRepository<UserSettingEntity, Long>, CustomAlertRepository {
    Optional<UserSettingEntity> findByUser(UserEntity user);
}
