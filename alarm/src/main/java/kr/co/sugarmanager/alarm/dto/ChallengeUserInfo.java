package kr.co.sugarmanager.alarm.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = ChallengeUserInfo.class)
public class ChallengeUserInfo {

    @JsonAlias(value = "nickname")
    private String nickname;

    @JsonAlias(value = "fcmToken")
    private String fcmToken;

    @JsonAlias(value = "challengeTitle")
    private String challengeTitle;

    @JsonAlias(value = "challenge_alert_hour")
    private int hour;

    @JsonAlias(value = "challenge_alert_min")
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
    public ChallengeUserInfo(Map<String, Object> userInfoMap){
        this(userInfoMap.get("nickname"),
                userInfoMap.get("fcmToken"),
                userInfoMap.get("challengeTitle"),
                userInfoMap.get("hour"),
                userInfoMap.get("minute"));
    }

    public ChallengeUserInfo(Object nickname, Object fcmToken, Object challengeTitle, Object hour, Object minute){
        setNickname((String) nickname);
        setFcmToken((String) fcmToken);
        setChallengeTitle((String) challengeTitle);
        setHour((int) hour);
        setMinute((int) minute);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ChallengeUserInfobuilder {}
}
