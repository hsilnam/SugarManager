package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private String title;
    private String body;
    private String fcmToken;
}

