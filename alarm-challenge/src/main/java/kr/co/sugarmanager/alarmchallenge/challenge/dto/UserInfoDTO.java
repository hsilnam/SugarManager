package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private String nickname;
    private String fcmToken;
    private String challengeTitle;
    private int hour;
    private int minute;
}
