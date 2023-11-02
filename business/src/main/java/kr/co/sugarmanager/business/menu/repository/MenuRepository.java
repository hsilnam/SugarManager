package kr.co.sugarmanager.business.menu.repository;

import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
}
