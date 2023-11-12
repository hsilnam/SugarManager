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
    public ResponseEntity<ChallengeAddDTO.Response> addChallenge(@RequestBody ChallengeAddDTO.Request dto){
        ChallengeAddDTO.Response response = challengeService.addChallenge(dto);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/delete")
    public ResponseEntity<ChallengeDeleteDTO.Response> deleteChallenge(@RequestBody ChallengeDeleteDTO.Request dto){
        ChallengeDeleteDTO.Response response = challengeService.deleteChallenge(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{nickname}")
    public ResponseEntity<UserChallengeAllDTO.Response> userChallengesAll(@PathVariable String nickname){
        UserChallengeAllDTO.Response response = challengeService.userChallengesAll(nickname);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userPk}/{challengePk}")
    public ResponseEntity<UserChallengeInfoDTO.Response> userChallengeInfo(@PathVariable Long userPk, @PathVariable Long challengePk){
        UserChallengeInfoDTO.Response response = challengeService.userChallengeInfo(userPk, challengePk);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/poke/info")
    public ResponseEntity<ChallengePokeDTO.Response> infoForPoke(ChallengePokeDTO.Request dto){
        ChallengePokeDTO.Response response = challengeService.infoForPoke(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
