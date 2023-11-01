package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto.AlarmBloodsugarDTO;
import kr.co.sugarmanger.alarmBloodsugar.bloodsugar.service.AlarmBloodsugarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmBloodsugarController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AlarmBloodsugarService alarmBloodsugarService;

    String TOPIC = "alarm";
    @GetMapping("/bloodsugar")
    public ResponseEntity<AlarmBloodsugarDTO.Response> todaysChallanges() throws JsonProcessingException {
        AlarmBloodsugarDTO.Response response = alarmBloodsugarService.bloodSugarAlarms();
        String stringResponse = new ObjectMapper().writeValueAsString(response);
        kafkaTemplate.send(TOPIC,stringResponse);
        kafkaTemplate.flush();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

