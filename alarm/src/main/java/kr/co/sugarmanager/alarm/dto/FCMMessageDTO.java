package kr.co.sugarmanager.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FCMMessageDTO {
    private String title;
    private String body;
    private String fcmToken;
}
