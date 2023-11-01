package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private String type;

    @JsonAlias(value = "nickname")
    private String nickname;

    @JsonAlias(value = "fcmToken")
    private String fcmToken;

    @JsonAlias(value = "challengeAlert")
    private boolean challengeAlert;

    @JsonAlias(value = "challenge_alert_hour")
    private int hour;

    @JsonAlias(value = "challenge_alert_min")
    private int minute;
}

