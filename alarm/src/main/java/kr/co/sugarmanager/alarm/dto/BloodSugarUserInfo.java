package kr.co.sugarmanager.alarm.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = ChallengeUserInfo.class)
public class BloodSugarUserInfo {

    @JsonAlias(value = "nickname")
    private String nickname;

    @JsonAlias(value = "fcmToken")
    private String fcmToken;

    // set
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }

    public BloodSugarUserInfo(Map<String,Object> userInfoMap){
        this(userInfoMap.get("nickname"),
                userInfoMap.get("fcmToken"));
    }

    public BloodSugarUserInfo(Object nickname, Object fcmToken){
        setNickname((String) nickname);
        setFcmToken((String) fcmToken);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class BloodSugarUserInfobuilder {}
}
