package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.sugarmanager.userservice.user.vo.AlertType;
import lombok.*;

public class AlarmUpdateDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;
        private AlertType category;
        private boolean status;
        private Integer hour;//category가 BLOODSUGAR인 경우, 식후 몇시간후 알림을 보내줄지 셋팅하는 변수
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;
        private AlertType category;
        private boolean status;
        private Integer hour;//category가 BLOODSUGAR인 경우, 식후 몇시간후 알림을 보내줄지 셋팅하는 변수
    }
}