package kr.co.sugarmanager.alarmchallenge.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.AlarmChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.RemindChallengeDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.dto.TodayChallengesDTO;
import kr.co.sugarmanager.alarmchallenge.challenge.service.AlarmChallengeService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmChallengeController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AlarmChallengeService alarmChallengeService;

    @Value(value = "${TOPIC-TODAY}")
    private String TODAY;

    @Scheduled(cron = "0 0 0 * * *")
    @GetMapping("/challenge/today")
    public ResponseEntity<TodayChallengesDTO.Response> todaysChallenges() throws JsonProcessingException {
        TodayChallengesDTO.Response response = alarmChallengeService.todaysChallenges();
        String stringResponse = new ObjectMapper().writeValueAsString(response);
        kafkaTemplate.send(TODAY, stringResponse);
        kafkaTemplate.flush();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Value(value = "${TOPIC-CHALLENGE}")
    private String CHALLENGE;

    @Scheduled(cron = "0 */9 * * * *")
    @GetMapping("/challenge")
    public ResponseEntity<AlarmChallengeDTO.Response> getChallanges() throws JsonProcessingException {
        AlarmChallengeDTO.Response response = alarmChallengeService.getChallanges();
        String stringResponse = new ObjectMapper().writeValueAsString(response);
        kafkaTemplate.send(CHALLENGE,stringResponse);
        kafkaTemplate.flush();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Value(value = "${TOPIC-REMIND}")
    private String REMIND;

    @Scheduled(cron = " 0 0 20 * * *")
    @GetMapping("/challenge/remind")
    public ResponseEntity<RemindChallengeDTO.Response> remind() throws JsonProcessingException {
        RemindChallengeDTO.Response response = alarmChallengeService.remind();
        String stringResponse = new ObjectMapper().writeValueAsString(response);
        kafkaTemplate.send(REMIND,stringResponse);
        kafkaTemplate.flush();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
