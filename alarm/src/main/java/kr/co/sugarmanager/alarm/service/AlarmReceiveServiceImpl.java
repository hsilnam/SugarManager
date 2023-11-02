package kr.co.sugarmanager.alarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.alarm.dto.BloodSugarUserInfo;
import kr.co.sugarmanager.alarm.dto.ChallengeUserInfo;
import kr.co.sugarmanager.alarm.dto.FCMMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmReceiveServiceImpl implements AlarmReceiveService {
    private final ObjectMapper objectMapper;
    private final FCMService fcmService;

    @Override
    @KafkaListener(topics = "alarm-challenge")
    public void consumeChallengeAlarm(String kafkaMessage){

        log.info("message : {}", kafkaMessage);
        try {
            Map<String, Object> map = objectMapper.readValue(kafkaMessage, Map.class);
            ArrayList<Object> userInfoMap = (ArrayList<Object>) map.get("userInfos");

            for (Object userObject : userInfoMap){
                ChallengeUserInfo userInfo = new ChallengeUserInfo((Map<String, Object>) userObject);

                // message body
                StringBuilder body = new StringBuilder();
                body.append(userInfo.getNickname()).append("님 ").append(userInfo.getChallengeTitle()).append(" 챌린지를 완료하셨나요?");

                FCMMessageDTO dto = FCMMessageDTO.builder()
                        .title("알림")
                        .body(body.toString())
                        .fcmToken(userInfo.getFcmToken())
                        .build();

                fcmService.send(dto);
            }


        } catch (JsonProcessingException e){
            log.info("error : {} ", e.getMessage());
        }
    }

    @Override
    @KafkaListener(topics = "alarm-bloodsugar")
    public void consumeBloodSugarAlarm(String kafkaMessage) {
        log.info("message : {}", kafkaMessage);
        try {
            Map<String, Object> map = objectMapper.readValue(kafkaMessage, Map.class);
            ArrayList<Object> userInfoMap = (ArrayList<Object>) map.get("userInfos");

            for (Object userObject : userInfoMap){
                BloodSugarUserInfo userInfo = new BloodSugarUserInfo((Map<String, Object>) userObject);

                // message body
                StringBuilder body = new StringBuilder();
                body.append(userInfo.getNickname()).append("님 ").append(" 혈당을 기록할 시간이에요!");

                FCMMessageDTO dto = FCMMessageDTO.builder()
                        .title("알림")
                        .body(body.toString())
                        .fcmToken(userInfo.getFcmToken())
                        .build();

                fcmService.send(dto);
            }

        } catch (JsonProcessingException e){
            log.info("error : {} ", e.getMessage());
        }
    }



}
