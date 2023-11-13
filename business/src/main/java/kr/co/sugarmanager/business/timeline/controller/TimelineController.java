package kr.co.sugarmanager.business.timeline.controller;

import kr.co.sugarmanager.business.timeline.dto.TimelineDateDTO;
import kr.co.sugarmanager.business.timeline.dto.TimelineMonthDTO;
import kr.co.sugarmanager.business.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{nickname}/{year}/{month}/{date}")
    public ResponseEntity<TimelineDateDTO.Response> timelineDate(
//            @RequestHeader("X-Authrization-Id") Long id,
            @PathVariable String nickname,
            @PathVariable Integer year,
            @PathVariable Integer month,
            @PathVariable Integer date){
        TimelineDateDTO.Response response = timelineService.timelineDate(nickname,year,month,date); //id, nickname, year, month, date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
