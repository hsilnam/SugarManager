package kr.co.sugarmanager.search.controller;


import kr.co.sugarmanager.search.dto.NutrientsFindDTO;
import kr.co.sugarmanager.search.service.NutrientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
public class NutrientsController {
    private final NutrientsService nutrientsService;

    @GetMapping(value = "/{nutrientsName}")
    public ResponseEntity<NutrientsFindDTO.Response> find(
            @PathVariable("nutrientsName") String nutrientsName) {
        log.info("nutrientsName find - nutrientsName{}", nutrientsName);
        return new ResponseEntity<>(nutrientsService.find(nutrientsName), HttpStatus.OK);
    }
}
