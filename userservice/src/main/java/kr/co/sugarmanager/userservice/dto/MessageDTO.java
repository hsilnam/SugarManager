package kr.co.sugarmanager.userservice.dto;

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