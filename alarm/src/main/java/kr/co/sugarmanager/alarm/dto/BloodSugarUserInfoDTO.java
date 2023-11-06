package kr.co.sugarmanager.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = ChallengeUserInfoDTO.class)
public class BloodSugarUserInfoDTO {

    private String nickname;
    private String fcmToken;

    // set
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }

    public BloodSugarUserInfoDTO(Map<String,Object> userInfoMap){
        this(userInfoMap.get("nickname"),
                userInfoMap.get("fcmToken"));
    }

    public BloodSugarUserInfoDTO(Object nickname, Object fcmToken){
        setNickname((String) nickname);
        setFcmToken((String) fcmToken);
    }
}
