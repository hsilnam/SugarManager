package kr.co.sugarmanager.search.repository;

import kr.co.sugarmanager.search.entity.NutrientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutrientsRepository extends JpaRepository<NutrientsEntity, Long> {
    List<NutrientsEntity> findTop20ByNutrientsNameContaining(String nutrientsName);
}
