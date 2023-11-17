package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

public class UserInfoUpdateDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;
        private String name;
        private String nickname;
        private Date birthday;
        private Integer height;
        private Integer weight;
        private Integer bloodSugarMin;
        private Integer bloodSugarMax;
        private String gender;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;
    }
}
