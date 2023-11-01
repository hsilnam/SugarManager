package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private String type;

    @JsonAlias(value = "nickname")
    private String nickname;

    @JsonAlias(value = "fcmToken")
    private String fcmToken;

}

