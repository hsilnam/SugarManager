package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

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
}

