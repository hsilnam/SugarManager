package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    private String type;
    private String nickname;
    private String fcmToken;

}

