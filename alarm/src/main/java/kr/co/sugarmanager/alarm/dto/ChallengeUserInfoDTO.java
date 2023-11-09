package kr.co.sugarmanager.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = ChallengeUserInfoDTO.class)
public class ChallengeUserInfoDTO {
    private String nickname;
    private String fcmToken;
    private String challengeTitle;
    private int hour;
    private int minute;

    // set
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public ChallengeUserInfoDTO(Map<String, Object> userInfoMap){
        this(userInfoMap.get("nickname"),
                userInfoMap.get("fcmToken"),
                userInfoMap.get("challengeTitle"),
                userInfoMap.get("hour"),
                userInfoMap.get("minute"));
    }

    public ChallengeUserInfoDTO(Object nickname, Object fcmToken, Object challengeTitle, Object hour, Object minute){
        setNickname((String) nickname);
        setFcmToken((String) fcmToken);
        setChallengeTitle((String) challengeTitle);
        setHour((int) hour);
        setMinute((int) minute);
    }
}
