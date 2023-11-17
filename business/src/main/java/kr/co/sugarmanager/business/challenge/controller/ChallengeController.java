package kr.co.sugarmanager.business.challenge.controller;

import kr.co.sugarmanager.business.challenge.dto.*;
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
@RequestMapping("/api/v1/challenge")
public class ChallengeController {
    private final ChallengeService challengeService;

    @Scheduled(cron = "0 0 0 * * *")
    @GetMapping("/reset")
    public ResponseEntity<TodayChallengesDTO.Response> todaysChallenges() {
        TodayChallengesDTO.Response response = challengeService.todaysChallenges();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ChallengeAddDTO.Response> addChallenge(
            @RequestHeader("X-Authorization-Id") Long pk,
            @RequestBody ChallengeAddDTO.Request dto){
        ChallengeAddDTO.Response response = challengeService.addChallenge(pk, dto);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/delete")
    public ResponseEntity<ChallengeDeleteDTO.Response> deleteChallenge(
            @RequestHeader("X-Authorization-Id") Long pk,
            @RequestBody ChallengeDeleteDTO.Request dto){
        ChallengeDeleteDTO.Response response = challengeService.deleteChallenge(pk, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{nickname}")
    public ResponseEntity<UserChallengeAllDTO.Response> userChallengesAll(
            @RequestHeader("X-Authorization-Id") Long pk,
            @PathVariable String nickname){
        UserChallengeAllDTO.Response response = challengeService.userChallengesAll(pk, nickname);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{nickname}/{challengePk}")
    public ResponseEntity<UserChallengeInfoDTO.Response> userChallengeInfo(
            @RequestHeader("X-Authorization-Id") Long pk,
            @PathVariable String nickname,
            @PathVariable Long challengePk){
        UserChallengeInfoDTO.Response response = challengeService.userChallengeInfo(pk, nickname, challengePk);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{challengePk}")
    public ResponseEntity<ChallengeClaimDTO.Response> claim(
            @RequestHeader("X-Authorization-Id") Long pk,
            @PathVariable Long challengePk){
        ChallengeClaimDTO.Response response = challengeService.claim(pk,challengePk);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
