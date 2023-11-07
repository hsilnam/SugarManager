package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RemindUserInfoDTO {
    private String nickname;
    private String fcmToken;
}
