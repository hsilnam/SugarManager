package kr.co.sugarmanger.alarmBloodsugar.bloodsugar.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class AlarmBloodsugarDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @Builder.Default
        List<UserInfo> userInfos = new ArrayList<>();

    }
}

