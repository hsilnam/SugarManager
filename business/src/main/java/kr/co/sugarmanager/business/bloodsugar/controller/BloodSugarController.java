package kr.co.sugarmanager.business.bloodsugar.controller;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarDeleteDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSaveDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarSelectDTO;
import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarUpdateDTO;
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
            @RequestHeader("Authorization") String auth,
            @Validated @RequestBody BloodSugarSaveDTO.Request bloodSugarDTO) {
        log.info("BloodSugar Save - Authorization: {}, menuDto: {}", auth, bloodSugarDTO);
        Long userPk = 1L;       // TODO: 서버간 통신 필요
        return new ResponseEntity<>(bloodSugarService.save(userPk, bloodSugarDTO), HttpStatus.CREATED);
    }

    @PostMapping(value = "/edit", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BloodSugarUpdateDTO.Response> edit(
            @RequestHeader("Authorization") String auth,
            @Validated @RequestBody BloodSugarUpdateDTO.Request bloodSugarDTO) {
        log.info("BloodSugar Update - Authorization: {}, menuDto: {}", auth, bloodSugarDTO);
        Long userPk = 1L;       // TODO: 서버간 통신 필요
        return new ResponseEntity<>(bloodSugarService.update(userPk, bloodSugarDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/delete", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BloodSugarDeleteDTO.Response> delete(
            @RequestHeader("Authorization") String auth,
            @Validated @RequestBody BloodSugarDeleteDTO.Request bloodSugarDTO) {
        log.info("BloodSugar delete - Authorization: {}, menuDto: {}", auth, bloodSugarDTO);
        Long userPk = 1L;       // TODO: 서버간 통신 필요
        return new ResponseEntity<>(bloodSugarService.delete(userPk, bloodSugarDTO), HttpStatus.OK);
    }

    @GetMapping(value = "/{nickname}/{year}/{month}/{day}")
    public ResponseEntity<BloodSugarSelectDTO.Response> selet(
            @RequestHeader("Authorization") String auth,
            @PathVariable("nickname") String nickname,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @PathVariable("day") int day) {
        log.info("BloodSugar select - Authorization: {}, nickname: {}, date: {}-{}-{}", auth, nickname, year, month, day);
        Long userPkByNickname = 1L;       // TODO: 서버간 통신 필요 (auth와 같은 그룹인지, 같은 그룹이라면 PK가져오기)
        return new ResponseEntity<>(bloodSugarService.select(userPkByNickname, year, month, day), HttpStatus.OK);
    }
}
