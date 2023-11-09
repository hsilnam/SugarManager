package kr.co.sugarmanager.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = KafkaMessageDTO.class)
public class KafkaMessageDTO {
    private String title;
    private String body;
    private String fcmToken;

    // set
    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }


    public KafkaMessageDTO(Map<String, Object> userInfoMap){
        this(userInfoMap.get("title"),
                userInfoMap.get("body"),
                userInfoMap.get("fcmToken"));
    }

    public KafkaMessageDTO(Object title, Object body, Object fcmToken){
        setTitle((String) title);
        setBody((String) body);
        setFcmToken((String) fcmToken);
    }
}