package kr.co.sugarmanager.alarm.challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.alarm.dto.ChallengeUserInfo;
import kr.co.sugarmanager.alarm.dto.FCMMessageDTO;
import kr.co.sugarmanager.alarm.service.FCMService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class ChallengeServiceTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FCMService fcmService;

    @Test
    void deserializeChallengeAlarm() throws Exception{
        String kafkaMessage = "{\n" +
                "\t\"userInfos\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"nickname\": \"wangbabo\",\n" +
                "\t\t\t\"fcmToken\": \"hsilnambabo\",\n" +
                "\t\t\t\"challengeTitle\": \"남현실바보\",\n" +
                "\t\t\t\"hour\": 10,\n" +
                "\t\t\t\"minute\": 20\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"nickname\": \"wangbabo\",\n" +
                "\t\t\t\"fcmToken\": \"hsilnambabo\",\n" +
                "\t\t\t\"challengeTitle\": \"남현실바보\",\n" +
                "\t\t\t\"hour\": 10,\n" +
                "\t\t\t\"minute\": 20\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"nickname\": \"wangbabo\",\n" +
                "\t\t\t\"fcmToken\": \"hsilnambabo\",\n" +
                "\t\t\t\"challengeTitle\": \"남현실바보\",\n" +
                "\t\t\t\"hour\": 10,\n" +
                "\t\t\t\"minute\": 20\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";


        Map<String, Object> map = objectMapper.readValue(kafkaMessage, Map.class);
        ArrayList<Object> userInfoMap = (ArrayList<Object>) map.get("userInfos");

        for (Object userObject : userInfoMap) {
            ChallengeUserInfo userInfo = new ChallengeUserInfo((Map<String, Object>) userObject);

            // message body
            StringBuilder body = new StringBuilder();
            body.append(userInfo.getNickname()).append("님 ").append(userInfo.getChallengeTitle()).append(" 챌린지를 완료하셨나요?");

            System.out.println("================");
            System.out.println(body);

            FCMMessageDTO dto = FCMMessageDTO.builder()
                    .title("알림")
                    .body(body.toString())
                    .fcmToken(userInfo.getFcmToken())
                    .build();

            fcmService.send(dto);
        }
    }
}
