package kr.co.sugarmanager.business.timeline.controller;

import kr.co.sugarmanager.business.timeline.dto.TimelineMonthDTO;
import kr.co.sugarmanager.business.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/timeline")
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/{nickname}/{year}/{month}")
    public ResponseEntity<TimelineMonthDTO.Response> timelineMonth(
//            @RequestHeader("X-Authrization-Id") Long id,
            @PathVariable String nickname,
            @PathVariable Integer year,
            @PathVariable Integer month){
        TimelineMonthDTO.Response response = timelineService.timelineMonth(nickname,year,month); //id, nickname, year, month);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
