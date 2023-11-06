package kr.co.sugarmanager.alarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.alarm.dto.BloodSugarUserInfoDTO;
import kr.co.sugarmanager.alarm.dto.ChallengeUserInfoDTO;
import kr.co.sugarmanager.alarm.dto.FCMMessageDTO;
import kr.co.sugarmanager.alarm.dto.PokeUserInfoDTO;
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
                ChallengeUserInfoDTO userInfo = new ChallengeUserInfoDTO((Map<String, Object>) userObject);

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
                BloodSugarUserInfoDTO userInfo = new BloodSugarUserInfoDTO((Map<String, Object>) userObject);

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

    @Override
    @KafkaListener(topics = "alarm-poke")
    public void consumePokeAlarm(String kafkaMessage) {
        try{
            Map<String,Object> map = objectMapper.readValue(kafkaMessage, Map.class);
            ArrayList<Object> userInfoMap = (ArrayList<Object>) map.get("userInfos");

            for (Object userObject : userInfoMap){
                PokeUserInfoDTO userInfo = new PokeUserInfoDTO((Map<String, Object>) userObject);

                // message body
                StringBuilder body = new StringBuilder();
                body
                        .append(userInfo.getTargetNickname())
                        .append("님, ")
                        .append(userInfo.getNickname())
                        .append("님께서 ")
                        .append(userInfo.getChallengeTitle())
                        .append(" 챌린지 달성 응원을 보냈습니다!");

                FCMMessageDTO dto = FCMMessageDTO.builder()
                        .title("알림")
                        .body(body.toString())
                        .fcmToken(userInfo.getFcmToken())
                        .build();

                fcmService.send(dto);

            }
        }catch (JsonProcessingException e){
            log.info("error: {}", e.getMessage());
        }
    }
}
