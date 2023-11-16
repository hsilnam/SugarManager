package kr.co.sugarmanager.userservice.user.repository;

import kr.co.sugarmanager.userservice.user.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {
}
