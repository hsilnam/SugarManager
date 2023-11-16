package kr.co.sugarmanager.search.service;

import kr.co.sugarmanager.search.dto.NutrientsFindDTO;
import kr.co.sugarmanager.search.entity.NutrientsEntity;
import kr.co.sugarmanager.search.repository.NutrientsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutrientsServiceImpl implements NutrientsService{
    private final NutrientsRepository nutrientsRepository;
    @Override
    public NutrientsFindDTO.Response find(String name) {
        List<NutrientsEntity> findResult = nutrientsRepository.findTop20ByNutrientsNameContaining(name);
        return NutrientsFindDTO.Response.builder()
                .success(true)
                .response(findResult)
                .error(null)
                .build();
    }
}
