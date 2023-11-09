package kr.co.sugarmanager.business.tip.controller;

import kr.co.sugarmanager.business.tip.dto.TipDTO;
import kr.co.sugarmanager.business.tip.service.TipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/tip")
public class TipController {
    private final TipService tipService;

    @GetMapping("/")
    public ResponseEntity<TipDTO.Response> tipOfTheDay(){
        TipDTO.Response response = tipService.tipOfTheDay();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
