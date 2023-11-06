package kr.co.sugarmanager.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = PokeUserInfoDTO.class)
public class PokeUserInfoDTO {
    private String nickname;
    private String fcmToken;
    private String targetNickname;
    private String challengeTitle;

    // set
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public void setTargetNickname(String targetNickname){
        this.targetNickname = targetNickname;
    }
    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }
    public PokeUserInfoDTO(Map<String, Object> userInfoMap){
        this(userInfoMap.get("nickname"),
                userInfoMap.get("fcmToken"),
                userInfoMap.get("targetNickname"),
                userInfoMap.get("challengeTitle"));
    }
    public PokeUserInfoDTO(Object nickname, Object fcmToken, Object targetNickname, Object challengeTitle) {
        setNickname((String) nickname);
        setFcmToken((String) fcmToken);
        setTargetNickname((String) targetNickname);
        setChallengeTitle((String) challengeTitle);
    }
}
