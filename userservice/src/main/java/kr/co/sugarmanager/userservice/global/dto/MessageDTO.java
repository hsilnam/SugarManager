package kr.co.sugarmanager.userservice.global.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageDTO {
    private String title;
    private String body;
    private String fcmToken;
}