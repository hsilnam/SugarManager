package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    @JsonAlias(value = "fcmToken")
    private String fcmToken;

    @JsonAlias(value = "challengeAlert")
    private boolean challengeAlert;

    @JsonAlias(value = "challenge_alert_hour")
    private int hour;

    @JsonAlias(value = "challenge_alert_min")
    private int minute;

    @JsonAlias(value = "deleted_at")
    private LocalDateTime deleted_at;

}

