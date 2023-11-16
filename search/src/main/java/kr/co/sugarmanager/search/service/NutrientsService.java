package kr.co.sugarmanager.search.service;

import kr.co.sugarmanager.search.dto.NutrientsFindDTO;

public interface NutrientsService {
    NutrientsFindDTO.Response find(String name);
}
