package kr.co.sugarmanager.alarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.alarm.dto.FCMMessageDTO;
import kr.co.sugarmanager.alarm.dto.KafkaMessageDTO;
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
    @KafkaListener(topics = "${TOPIC}")
    public void consumeAlarm(String kafkaMessage){

        log.info("message : {}", kafkaMessage);
        try {
            Map<String, Object> map = objectMapper.readValue(kafkaMessage, Map.class);
            ArrayList<Object> userInfoMap = (ArrayList<Object>) map.get("userInfos");

            for (Object userObject : userInfoMap){
                KafkaMessageDTO userInfo = new KafkaMessageDTO((Map<String, Object>) userObject);

                FCMMessageDTO dto = FCMMessageDTO.builder()
                        .title(userInfo.getTitle())
                        .body(userInfo.getBody())
                        .fcmToken(userInfo.getFcmToken())
                        .build();

                if (dto.getTitle() != null && dto.getBody() != null && dto.getFcmToken() != null) {
                    fcmService.send(dto);
                }
            }


        } catch (JsonProcessingException e){
            log.info("error : {} ", e.getMessage());
        }
    }
}
