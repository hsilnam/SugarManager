package kr.co.sugarmanager.userservice.repository;

import kr.co.sugarmanager.userservice.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {
}
