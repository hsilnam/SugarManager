package kr.co.sugarmanager.search.repository;

import kr.co.sugarmanager.search.entity.NutrientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutrientsRepository extends JpaRepository<NutrientsEntity, Long> {
    List<NutrientsEntity> findTop20ByNutrientsNameContaining(String nutrientsName);
}
