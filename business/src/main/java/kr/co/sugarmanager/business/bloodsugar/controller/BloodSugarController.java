package kr.co.sugarmanager.business.bloodsugar.controller;

import kr.co.sugarmanager.business.bloodsugar.dto.*;
import kr.co.sugarmanager.business.bloodsugar.service.BloodSugarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/v1/bloodsugar")
@Slf4j
@RequiredArgsConstructor
public class BloodSugarController {

    private final BloodSugarService bloodSugarService;

    @PostMapping(value = "/save", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BloodSugarSaveDTO.Response> save(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @Validated @RequestBody BloodSugarSaveDTO.Request bloodSugarDTO) {
        log.info("BloodSugar Save - userPk: {}, menuDto: {}", userPk, bloodSugarDTO);
        return new ResponseEntity<>(bloodSugarService.save(userPk, bloodSugarDTO), HttpStatus.CREATED);
    }

    @PostMapping(value = "/edit", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BloodSugarUpdateDTO.Response> edit(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @Validated @RequestBody BloodSugarUpdateDTO.Request bloodSugarDTO) {
        log.info("BloodSugar Update - userPk: {}, menuDto: {}", userPk, bloodSugarDTO);
        return new ResponseEntity<>(bloodSugarService.update(userPk, bloodSugarDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/delete", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BloodSugarDeleteDTO.Response> delete(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @Validated @RequestBody BloodSugarDeleteDTO.Request bloodSugarDTO) {
        log.info("BloodSugar delete - userPk: {}, menuDto: {}", userPk, bloodSugarDTO);
        return new ResponseEntity<>(bloodSugarService.delete(userPk, bloodSugarDTO), HttpStatus.OK);
    }

    @GetMapping(value = "/{nickname}/{year}/{month}/{day}")
    public ResponseEntity<BloodSugarSelectDTO.Response> selet(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @PathVariable("nickname") String nickname,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @PathVariable("day") int day) {
        log.info("BloodSugar select - userPk: {}, nickname: {}, date: {}-{}-{}", userPk, nickname, year, month, day);
        Long userPkByNickname = 1L;
        return new ResponseEntity<>(bloodSugarService.select(userPk, nickname, year, month, day), HttpStatus.OK);
    }

    @GetMapping(value = "/period/{nickname}/{startDate}/{endDate}/{page}")
    public ResponseEntity<BloodSugarPeriodDTO.Response> seletUsingPeriod(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @PathVariable("nickname") String nickname,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate,
            @PathVariable("page") Integer page) {
        Long userPkByNickname = 1L;
        log.info("BloodSugar Period Select - userPk: {}, nickname: {}, startDate: {}, endDate: {}, page: {},", userPk, nickname, startDate, endDate, page);

        return new ResponseEntity<>(bloodSugarService.selectPeriod(userPk, nickname, startDate, endDate, page), HttpStatus.OK);
    }
}
