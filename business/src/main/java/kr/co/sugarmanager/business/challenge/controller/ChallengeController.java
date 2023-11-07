package kr.co.sugarmanager.business.challenge.controller;

import kr.co.sugarmanager.business.challenge.dto.ChallengeAddDTO;
import kr.co.sugarmanager.business.challenge.dto.TodayChallengesDTO;
import kr.co.sugarmanager.business.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
public class ChallengeController {
    private final ChallengeService challengeService;

    @Scheduled(cron = "0 0 0 * * *")
    @GetMapping("/reset")
    public ResponseEntity<TodayChallengesDTO.Response> todaysChallenges() {
        TodayChallengesDTO.Response response = challengeService.todaysChallenges();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ChallengeAddDTO.Response> addChallenge(Long userPk, @RequestBody ChallengeAddDTO.Request dto){
        ChallengeAddDTO.Response response = challengeService.addChallenge(userPk, dto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
