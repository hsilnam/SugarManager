package kr.co.sugarmanager.business.tip.repository;

import kr.co.sugarmanager.business.tip.entity.TipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TipRepository extends JpaRepository<TipEntity, Long> {
    @Query("select t FROM TipEntity t where t.deletedAt is null order by rand() limit 1")
    TipEntity findTipOfTheDay();
}
