package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto;

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

